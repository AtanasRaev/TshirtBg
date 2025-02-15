package bg.tshirt.service;

import bg.tshirt.database.dto.ClothingDTO;
import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    Image uploadImage(MultipartFile multipartFile, String publicId, Clothing cloth);

    void saveAll(List<Image> extractedImages);

    void deleteAll(List<String> publicIds);

    Image saveImageInCloud(MultipartFile file, Clothing cloth, String side);

    Image findByPath(String path);

    void deleteImage(Image byPath);

    void save(Image image);
}
