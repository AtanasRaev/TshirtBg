package bg.tshirt.service.impl;

import bg.tshirt.database.dto.ClothingDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothingDetailsPageDTO;
import bg.tshirt.database.dto.ClothingPageDTO;
import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Image;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.enums.Type;
import bg.tshirt.database.repository.ClothingRepository;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothingService;
import bg.tshirt.service.ImageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ClothingServiceImpl implements ClothingService {
    private final ClothingRepository clothRepository;
    private final ImageService imageService;
    private final ModelMapper modelMapper;

    public ClothingServiceImpl(ClothingRepository clothRepository,
                               ImageService imageService,
                               ModelMapper modelMapper) {
        this.clothRepository = clothRepository;
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean addCloth(ClothingDTO clothDTO) {
        Optional<Clothing> optional = this.clothRepository.findByModelAndTypeAndGender(clothDTO.getModel(), clothDTO.getType(), clothDTO.getGender());

        if (optional.isPresent()) {
            return false;
        }

        Clothing cloth = new Clothing(clothDTO.getName(),
                clothDTO.getDescription(),
                setPrice(clothDTO.getType()),
                clothDTO.getModel() + getModelType(clothDTO.getType()),
                clothDTO.getType(),
                clothDTO.getGender(),
                clothDTO.getCategory());

        List<Image> images = new ArrayList<>();
        addNewImages(clothDTO, cloth, images);
        cloth.setImages(images);

        this.clothRepository.save(cloth);
        this.imageService.saveAll(images);
        return true;
    }

    @Override
    public ClothingDetailsPageDTO findById(Long id) {
        return this.clothRepository.findById(id)
                .map(cloth -> this.modelMapper.map(cloth, ClothingDetailsPageDTO.class))
                .orElse(null);
    }

    @Override
    public boolean editCloth(ClothEditDTO clothDto, Long id) {
        Clothing cloth = this.clothRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cloth with id: %d is not found", id)));

        if (isInvalidUpdate(clothDto, cloth)) {
            return false;
        }

        setClothDetails(cloth, clothDto);

        List<Image> updatedImages = processImages(clothDto, cloth);

        cloth.setImages(updatedImages);
        this.clothRepository.save(cloth);
        this.imageService.saveAll(updatedImages);

        return !updatedImages.isEmpty();
    }

    @Override
    public Page<ClothingPageDTO> findByQuery(Pageable pageable, String query) {
        return this.clothRepository.findByQuery(pageable, "%" + query + "%")
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByCategory(Pageable pageable, String query) {
        return this.clothRepository.findByCategory(pageable, query)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByType(Pageable pageable, String type) {
        return this.clothRepository.findByType(pageable, type)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> findByTypeAndCategory(Pageable pageable, String type, String category) {
        return this.clothRepository.findByTypeAndCategory(pageable, type, category)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public void setTotalSales(List<OrderItem> items, String newStatus, String oldStatus) {
        List<Clothing> allById = this.clothRepository.findAllById(items.stream().mapToLong(item -> item.getCloth().getId()).boxed().toList());
        allById.forEach(Clothing::updateTotalSales);
    }

    @Override
    public Page<ClothingPageDTO> getNewest(Pageable pageable) {
        return this.clothRepository.findAllDesc(pageable)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> getNewest(Pageable pageable, String type) {
        return this.clothRepository.findAllWithTypeDesc(pageable, type)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public Page<ClothingPageDTO> getMostSold(Pageable pageable) {
        return this.clothRepository.findAllOrderBySaleCount(pageable)
                .map(cloth -> this.modelMapper.map(cloth, ClothingPageDTO.class));
    }

    @Override
    public boolean delete(Long id) {
        Optional<Clothing> optional = this.clothRepository.findById(id);

        if (optional.isEmpty()) {
            return false;
        }

        List<String> publicIds = optional.get().getImages()
                .stream()
                .map(Image::getPublicId)
                .toList();

        this.imageService.deleteAll(publicIds);
        this.clothRepository.delete(optional.get());

        return true;
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
        cloth.setModel(clothDto.getModel() + getModelType(clothDto.getType()));
        cloth.setType(clothDto.getType());
        cloth.setGender(clothDto.getGender());
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
