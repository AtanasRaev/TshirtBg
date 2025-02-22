package bg.tshirt.service.impl;

import bg.tshirt.database.dto.*;
import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Image;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.enums.Category;
import bg.tshirt.database.entity.enums.Type;
import bg.tshirt.database.repository.ClothingRepository;
import bg.tshirt.exceptions.ClothingAlreadyExistsException;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothingService;
import bg.tshirt.service.ImageService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ClothingServiceImpl implements ClothingService {
    private final ClothingRepository clothingRepository;
    private final ImageService imageService;
    private final ModelMapper modelMapper;

    public ClothingServiceImpl(ClothingRepository clothRepository,
                               ImageService imageService,
                               ModelMapper modelMapper) {
        this.clothingRepository = clothRepository;
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean addClothing(ClothingDTO clothingDTO) {
        Optional<Clothing> optional = this.clothingRepository.findByModelAndType(clothingDTO.getModel(), clothingDTO.getType());

        if (optional.isPresent()) {
            return false;
        }

        Clothing clothing = new Clothing(clothingDTO.getName(),
                clothingDTO.getDescription(),
                setPrice(clothingDTO.getType()),
                clothingDTO.getModel().substring(0, 4) + getModelType(clothingDTO.getType()),
                clothingDTO.getType(),
                clothingDTO.getCategory());

        List<Image> images = new ArrayList<>();
        addNewImages(clothingDTO, clothing, images);
        clothing.setImages(images);

        this.clothingRepository.save(clothing);
        this.imageService.saveAll(images);
        return true;
    }

    @Override
    public ClothingDetailsPageDTO findById(Long id) {
        Optional<ClothingDetailsPageDTO> optional = this.clothingRepository.findById(id)
                .map(clothing -> this.modelMapper.map(clothing, ClothingDetailsPageDTO.class));

        if (optional.isEmpty()) {
            return null;
        }

        ClothingDetailsPageDTO clothing = optional.get();

        if (clothing.getType() != Type.KIT) {
            return clothing;
        }

        String modelTshirt = clothing.getModel().substring(0, clothing.getModel().length() - 2);
        String modelShorts = clothing.getModel().substring(0, clothing.getModel().length() - 1);

        addImagesToKit(clothing, modelTshirt);
        addImagesToKit(clothing, modelShorts);

        return clothing;
    }

    @Override
    public boolean editCloth(ClothEditDTO clothingDTO, Long id) {
        Clothing clothing = this.clothingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Clothing with id: %d is not found", id)));

        if (isInvalidUpdate(clothingDTO, clothing)) {
            return false;
        }

        Optional<Clothing> byModelAndType = this.clothingRepository.findByModelAndType(clothingDTO.getModel() + getModelType(clothingDTO.getType()), clothingDTO.getType());
        if (byModelAndType.isPresent()) {
            if (clothing.getId() != byModelAndType.get().getId()) {
                throw new ClothingAlreadyExistsException("Clothing with model " + clothingDTO.getModel() + " already exists.");
            }
        }

        setClothDetails(clothing, clothingDTO);

        List<Image> updatedImages = processImages(clothingDTO, clothing);

        clothing.setImages(updatedImages);
        this.clothingRepository.save(clothing);
        this.imageService.saveAll(updatedImages);

        return !updatedImages.isEmpty();
    }

    @Override
    public Page<ClothingPageDTO> findByQuery(Pageable pageable, String query) {
        return this.clothingRepository.findByQuery(pageable, "%" + query + "%")
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByQuery(Pageable pageable, String query, List<String> type) {
        return this.clothingRepository.findByQueryAndType(pageable, "%" + query + "%", type.stream().map(String::toLowerCase).toList())
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByCategory(Pageable pageable, List<String> category) {
        return this.clothingRepository.findByCategory(pageable, category.stream().map(String::toLowerCase).toList())
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByType(Pageable pageable, String type) {
        return this.clothingRepository.findByType(pageable, type)
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByTypeAndCategory(Pageable pageable, String type, List<String> category) {
        return this.clothingRepository.findByTypeAndCategory(pageable, type, category.stream().map(String::toLowerCase).toList())
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Override
    public void setTotalSales(List<OrderItem> items) {
        Map<Long, Integer> clothingQuantityMap = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getClothing().getId(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        List<Clothing> allById = this.clothingRepository.findAllById(clothingQuantityMap.keySet());

        allById.forEach(clothing -> {
            int totalQuantity = clothingQuantityMap.get(clothing.getId());
            for (int i = 0; i < totalQuantity; i++) {
                clothing.updateTotalSales();
            }
        });

        this.clothingRepository.saveAll(allById);
    }

    @Override
    public Page<ClothingPageDTO> getAllPage(Pageable pageable) {
        return this.clothingRepository.findAllPage(pageable)
                .map(clothing -> this.modelMapper.map(clothing, ClothingPageDTO.class));
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        Optional<Clothing> optional = this.clothingRepository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }
        Clothing clothing = optional.get();

        List<String> publicIds = clothing.getImages()
                .stream()
                .map(Image::getPublicId)
                .toList();
        this.imageService.deleteAll(publicIds);

        clothing.getImages().clear();

        this.clothingRepository.delete(clothing);
        return true;
    }


    @Override
    public Map<Category, Long> getClothingCountByCategories(String type) {
        List<Object[]> results = getCategoriesCount(type);
        Map<Category, Long> clothingCountMap = new HashMap<>();

        results.forEach(object -> {
            Category category = (Category) object[0];
            Long count = (Long) object[1];
            clothingCountMap.put(category, count);
        });

        return clothingCountMap;
    }

    private List<Object[]> getCategoriesCount(String type) {
        if (StringUtils.hasText(type)) {
            return this.clothingRepository.countClothingByCategory(type);
        } else {
            return this.clothingRepository.countClothingByCategory();
        }
    }

    private Double setPrice(Type type) {
        double price;

        switch (type) {
            case T_SHIRT -> price = 29.00;
            case SWEATSHIRT -> price = 54.00;
            case KIT -> price = 59.00;
            case SHORTS -> price = 30.00;
            default -> price = 37.00;
        }

        return price;
    }

    private String getModelType(Type type) {
        switch (type) {
            case SHORTS -> {
                return "K";
            }
            case SWEATSHIRT -> {
                return "SW";
            }
            case LONG_T_SHIRT -> {
                return "D";
            }
            case KIT -> {
                return "KT";
            }
            default -> {
                return "";
            }
        }
    }

    private void addImagesToKit(ClothingPageDTO clothing, String model) {
        this.clothingRepository.findByModel(model)
                .ifPresent(foundClothing -> {
                    List<ImagePageDTO> imageDTOs = foundClothing.getImages()
                            .stream()
                            .map(image -> this.modelMapper.map(image, ImagePageDTO.class))
                            .toList();
                    clothing.getImages().addAll(imageDTOs);
                });
    }

    private boolean isInvalidUpdate(ClothEditDTO clothDto, Clothing cloth) {
        boolean frontAndBackImagesEmpty = clothDto.getFrontImage() != null && clothDto.getFrontImage().isEmpty()
                && clothDto.getBackImage() != null && clothDto.getBackImage().isEmpty();
        boolean removingAllImages = clothDto.getRemovedImages().size() >= cloth.getImages().size();

        return frontAndBackImagesEmpty && removingAllImages;
    }

    private void setClothDetails(Clothing cloth, ClothEditDTO clothDto) {
        cloth.setName(clothDto.getName());
        cloth.setDescription(clothDto.getDescription());
        cloth.setPrice(setPrice(clothDto.getType()));
        cloth.setModel(clothDto.getModel().substring(0, 4) + getModelType(clothDto.getType()));
        cloth.setType(clothDto.getType());
        cloth.setCategory(clothDto.getCategory());
    }

    private List<Image> processImages(ClothEditDTO clothDto, Clothing cloth) {
        List<String> removedImagesPaths = clothDto.getRemovedImages();
        List<Image> imagesToSave = new ArrayList<>();

        if (!removedImagesPaths.isEmpty()) {
            removeImages(removedImagesPaths);
        }

        List<String> existingPaths = cloth.getImages()
                .stream()
                .map(Image::getPath)
                .filter(path -> !removedImagesPaths.contains(path))
                .toList();

        existingPaths.forEach(path -> {
            Image image = this.imageService.findByPath(path);
            if (image != null) {
                imagesToSave.add(image);
            }
        });

        addNewImages(clothDto, cloth, imagesToSave);

        return imagesToSave;
    }

    private void removeImages(List<String> removedImagesPaths) {
        removedImagesPaths.forEach(path -> {
            Image image = this.imageService.findByPath(path);
            if (image != null) {
                this.imageService.deleteImage(image);
            }
        });
    }

    private void addNewImages(ClothingDTO clothDto, Clothing cloth, List<Image> imagesToSave) {
        if (clothDto.getFrontImage() != null && !clothDto.getFrontImage().isEmpty()) {
            Image frontImage = this.imageService.saveImageInCloud(clothDto.getFrontImage(), cloth, "F");
            imagesToSave.add(frontImage);
        }

        if (clothDto.getBackImage() != null && !clothDto.getBackImage().isEmpty()) {
            Image backImage = this.imageService.saveImageInCloud(clothDto.getBackImage(), cloth, "B");
            imagesToSave.add(backImage);
        }
    }
}
