package bg.tshirt.database.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class ClothEditDTO extends ClothDTO {
    private MultipartFile frontImage;

    private MultipartFile backImage;

    private List<String> removedImages;

    public ClothEditDTO() {
        this.removedImages = new ArrayList<>();
    }

    public MultipartFile getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(MultipartFile frontImage) {
        this.frontImage = frontImage;
    }

    public MultipartFile getBackImage() {
        return backImage;
    }

    public void setBackImage(MultipartFile backImage) {
        this.backImage = backImage;
    }

    public List<String> getRemovedImages() {
        return removedImages;
    }

    public void setRemovedImages(List<String> removedImages) {
        this.removedImages = removedImages;
    }
}
