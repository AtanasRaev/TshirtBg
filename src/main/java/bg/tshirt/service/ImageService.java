package bg.tshirt.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile multipartFile, String publicId);
}
