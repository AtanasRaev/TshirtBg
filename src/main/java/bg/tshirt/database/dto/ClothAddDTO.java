package bg.tshirt.database.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class ClothAddDTO extends ClothDTO {
    @NotNull(message = "Image file is required.")
    private MultipartFile frontImage;

    private MultipartFile backImage;

    public @NotNull(message = "Image file is required.") MultipartFile getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(@NotNull(message = "Image file is required.") MultipartFile frontImage) {
        this.frontImage = frontImage;
    }

    public MultipartFile getBackImage() {
        return backImage;
    }

    public void setBackImage(MultipartFile backImages) {
        this.backImage = backImages;
    }
}
