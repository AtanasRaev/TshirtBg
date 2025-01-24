package bg.tshirt.service;

import bg.tshirt.database.dto.ClothAddDTO;
import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    Image uploadImage(MultipartFile multipartFile, String publicId, Cloth cloth);

    void saveAll(List<Image> extractedImages);

    void deleteAll(List<String> publicIds);

    Image saveImageInCloud(MultipartFile file, Cloth cloth, String side);

    Image findByPublicIds(String publicId);

    List<Image> saveImagesInCloud(ClothAddDTO clothDTO, Cloth cloth);

    Image findByPath(String path);

    void deleteImage(Image byPath);

    void save(Image image);
}
