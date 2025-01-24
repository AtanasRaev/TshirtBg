package bg.tshirt.service.impl;

import bg.tshirt.database.dto.ClothAddDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothPageDTO;
import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.Image;
import bg.tshirt.database.repository.ClothRepository;
import bg.tshirt.exceptions.NotFoundException;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.ImageService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


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
    public boolean addCloth(ClothAddDTO clothDTO) {
        Optional<Cloth> optional = this.clothRepository.findByModelAndTypeAndGender(clothDTO.getModel(), clothDTO.getType(), clothDTO.getGender());

        if (optional.isPresent()) {
            return false;
        }

        Cloth cloth = new Cloth(clothDTO.getName(),
                clothDTO.getDescription(),
                clothDTO.getPrice(),
                clothDTO.getModel(),
                clothDTO.getType(),
                clothDTO.getGender());


        List<Image> images = this.imageService.saveImagesInCloud(clothDTO, cloth);

        cloth.setImages(images);

        this.clothRepository.save(cloth);
        this.imageService.saveAll(images);
        return true;
    }

    @Override
    public ClothPageDTO findById(Long id) {
        Optional<Cloth> optional = this.clothRepository.findById(id);

        if (optional.isEmpty()) {
            return null;
        }

        Cloth cloth = optional.get();

        return this.modelMapper.map(cloth, ClothPageDTO.class);
    }

    @Override
    public boolean editCloth(ClothEditDTO clothDto, Long id) {
        Optional<Cloth> optional = this.clothRepository.findById(id);

        if (optional.isEmpty()) {
            throw new NotFoundException(String.format("Cloth with id: %d is not found", id));
        }

        Cloth cloth = optional.get();
        cloth.setName(clothDto.getName());
        cloth.setDescription(clothDto.getDescription());
        cloth.setPrice(clothDto.getPrice());
        cloth.setModel(clothDto.getModel());
        cloth.setType(clothDto.getType());
        cloth.setGender(clothDto.getGender());

        List<String> removedImagesPaths = clothDto.getRemovedImages();

        if (clothDto.getFrontImage() == null && clothDto.getBackImage() == null) {
            return false;
        }

        if (((clothDto.getFrontImage() != null && clothDto.getFrontImage().isEmpty()) && (clothDto.getBackImage() != null && clothDto.getBackImage().isEmpty())) &&
                (removedImagesPaths.size() >= cloth.getImages().size())) {
            return false;
        }

        List<Image> imagesToSave = new ArrayList<>();

        List<String> clothImages = new ArrayList<>(cloth.getImages()
                .stream()
                .map(Image::getPath)
                .toList());

        if (!removedImagesPaths.isEmpty()) {
            removedImagesPaths.forEach(path -> {
                Image byPath = this.imageService.findByPath(path);

                if (byPath != null) {
                    this.imageService.deleteImage(byPath);
                }
            });
        }

        clothImages.removeIf(removedImagesPaths::contains);

        clothImages.forEach(path -> {
            Image byPath = this.imageService.findByPath(path);

            if (byPath != null) {
                imagesToSave.add(byPath);
            }
        });

        if (clothDto.getFrontImage() != null && !clothDto.getFrontImage().isEmpty()) {
            Image front = this.imageService.saveImageInCloud(clothDto.getFrontImage(), cloth, "F");
            imagesToSave.add(front);
        }

        if (clothDto.getBackImage() != null && !clothDto.getBackImage().isEmpty()) {
            Image back = this.imageService.saveImageInCloud(clothDto.getBackImage(), cloth, "B");
            imagesToSave.add(back);
        }

        if (cloth.getImages().isEmpty()) {
            return false;
        }

        cloth.setImages(imagesToSave);
        this.clothRepository.save(cloth);
        this.imageService.saveAll(imagesToSave);
        return true;
    }
}
