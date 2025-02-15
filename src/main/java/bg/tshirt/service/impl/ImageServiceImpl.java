package bg.tshirt.service.impl;

import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.Image;
import bg.tshirt.database.repository.ImageRepository;
import bg.tshirt.service.ImageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;

    public ImageServiceImpl(ImageRepository imageRepository,
                            Cloudinary cloudinary) {
        this.imageRepository = imageRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Image uploadImage(MultipartFile multipartFile, String publicId, Clothing cloth) {
        try {
            Map<?, ?> uploadResult = this.cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId)
            );
            String secureUrl = (String) uploadResult.get("secure_url");
            return new Image(extractCloudinaryPath(secureUrl), publicId, cloth);
        } catch (IOException e) {
            System.err.println("Image upload failed: " + e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public void saveAll(List<Image> extractedImages) {
        extractedImages.forEach(image -> {
            if (image != null && this.imageRepository.findByPublicId(image.getPublicId()).isEmpty()) {
                this.imageRepository.save(image);
            }
        });
    }

    @Override
    public void deleteAll(List<String> publicIds) {
        List<Image> allByPublicIds = this.imageRepository.findAllByPublicIds(publicIds);
        allByPublicIds
                .forEach(image -> {
                    deleteImageFromCloudinary(image.getPublicId());
                });
        this.imageRepository.deleteAll(allByPublicIds);
    }

    @Override
    public Image saveImageInCloud(MultipartFile file, Clothing cloth, String side) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uniqueImageFront = cloth.getModel() + "_" + side;
        return uploadImage(file, uniqueImageFront, cloth);
    }

    @Override
    public Image findByPath(String path) {
        return this.imageRepository.findByPath(path).orElse(null);
    }

    @Override
    public void deleteImage(Image byPath) {
        deleteImageFromCloudinary(byPath.getPublicId());
        this.imageRepository.delete(byPath);
    }

    @Override
    public void save(Image image) {
        this.imageRepository.save(image);
    }

    private String extractCloudinaryPath(String url) {
        int startIndex = url.indexOf("/v");
        if (startIndex != -1) {
            return url.substring(startIndex);
        }
        throw new IllegalArgumentException("Invalid Cloudinary URL format.");
    }

    private void deleteImageFromCloudinary(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
