package bg.tshirt.service.impl;

import bg.tshirt.database.dto.ClothAddDTO;
import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.repository.ClothRepository;
import bg.tshirt.service.ClothService;
import bg.tshirt.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ClothServiceImpl implements ClothService {
    private final ClothRepository clothRepository;
    private final ImageService imageService;

    public ClothServiceImpl(ClothRepository clothRepository,
                            ImageService imageService) {
        this.clothRepository = clothRepository;
        this.imageService = imageService;
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

        String imageUrl = this.imageService.uploadImage(clothDTO.getImage(), cloth.getPageModel());
        String extractedPath = extractCloudinaryPath(imageUrl);
        cloth.setImage(extractedPath);

        this.clothRepository.save(cloth);
        return true;
    }

    private String extractCloudinaryPath(String url) {
        int startIndex = url.indexOf("/v");
        if (startIndex != -1) {
            return url.substring(startIndex);
        }
        throw new IllegalArgumentException("Invalid Cloudinary URL format.");
    }
}
