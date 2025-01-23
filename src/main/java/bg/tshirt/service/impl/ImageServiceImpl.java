package bg.tshirt.service.impl;

import bg.tshirt.service.ImageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {
    private final Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile multipartFile, String publicId) {
        try {
            Map<?, ?> uploadResult = this.cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId)
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            System.err.println("Image upload failed: " + e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
