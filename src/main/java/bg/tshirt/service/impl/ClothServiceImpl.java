package bg.tshirt.service.impl;

import bg.tshirt.database.dto.ClothDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothDetailsPageDTO;
import bg.tshirt.database.dto.ClothPageDTO;
import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.Image;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.repository.ClothRepository;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.ImageService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ClothServiceImpl implements ClothService {
    private final ClothRepository clothRepository;
    private final ImageService imageService;
    private final ModelMapper modelMapper;

    public ClothServiceImpl(ClothRepository clothRepository,
                            ImageService imageService,
                            ModelMapper modelMapper) {
        this.clothRepository = clothRepository;
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean addCloth(ClothDTO clothDTO) {
        Optional<Cloth> optional = this.clothRepository.findByModelAndTypeAndGender(clothDTO.getModel(), clothDTO.getType(), clothDTO.getGender());

        if (optional.isPresent()) {
            return false;
        }

        Cloth cloth = new Cloth(clothDTO.getName(),
                clothDTO.getDescription(),
                clothDTO.getPrice(),
                clothDTO.getModel(),
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
    public ClothDetailsPageDTO findById(Long id) {
        return this.clothRepository.findById(id)
                .map(cloth -> this.modelMapper.map(cloth, ClothDetailsPageDTO.class))
                .orElse(null);
    }

    @Override
    public boolean editCloth(ClothEditDTO clothDto, Long id) {
        Cloth cloth = this.clothRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cloth with id: %d is not found", id)));

        if (isInvalidUpdate(clothDto, cloth)) {
            return false;
        }

        updateClothDetails(cloth, clothDto);

        List<Image> updatedImages = processImages(clothDto, cloth);

        cloth.setImages(updatedImages);
        this.clothRepository.save(cloth);
        this.imageService.saveAll(updatedImages);

        return !updatedImages.isEmpty();
    }

    @Override
    public Page<ClothPageDTO> findByQuery(Pageable pageable, String query) {
        return this.clothRepository.findByQuery(pageable, "%" + query + "%")
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public Page<ClothPageDTO> findByCategory(Pageable pageable, String query) {
        return this.clothRepository.findByCategory(pageable, query)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public Page<ClothPageDTO> findByType(Pageable pageable, String type) {
        return this.clothRepository.findByType(pageable, type)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public Page<ClothPageDTO> findByTypeAndCategory(Pageable pageable, String type, String category) {
        return this.clothRepository.findByTypeAndCategory(pageable, type, category)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public void setTotalSales(List<OrderItem> items, String newStatus, String oldStatus) {
        List<Cloth> allById = this.clothRepository.findAllById(items.stream().mapToLong(item -> item.getCloth().getId()).boxed().toList());
        allById.forEach(Cloth::updateTotalSales);
    }

    @Override
    public Page<ClothPageDTO> getNewest(Pageable pageable) {
        return this.clothRepository.findAllDesc(pageable)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public Page<ClothPageDTO> getNewest(Pageable pageable, String type) {
        return this.clothRepository.findAllWithTypeDesc(pageable, type)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    @Override
    public Page<ClothPageDTO> getMostSold(Pageable pageable) {
        return this.clothRepository.findAllOrderBySaleCount(pageable)
                .map(cloth -> this.modelMapper.map(cloth, ClothPageDTO.class));
    }

    private boolean isInvalidUpdate(ClothEditDTO clothDto, Cloth cloth) {
        boolean frontAndBackImagesEmpty = clothDto.getFrontImage() != null && clothDto.getFrontImage().isEmpty()
                && clothDto.getBackImage() != null && clothDto.getBackImage().isEmpty();
        boolean removingAllImages = clothDto.getRemovedImages().size() >= cloth.getImages().size();

        return frontAndBackImagesEmpty && removingAllImages;
    }

    private void updateClothDetails(Cloth cloth, ClothEditDTO clothDto) {
        cloth.setName(clothDto.getName());
        cloth.setDescription(clothDto.getDescription());
        cloth.setPrice(clothDto.getPrice());
        cloth.setModel(clothDto.getModel());
        cloth.setType(clothDto.getType());
        cloth.setGender(clothDto.getGender());
        cloth.setCategory(clothDto.getCategory());
    }

    private List<Image> processImages(ClothEditDTO clothDto, Cloth cloth) {
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

    private void addNewImages(ClothDTO clothDto, Cloth cloth, List<Image> imagesToSave) {
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
