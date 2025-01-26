package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class ClothEditDTO extends ClothDTO {
    @JsonProperty("removed_images")
    private List<String> removedImages;

    public ClothEditDTO() {
        this.removedImages = new ArrayList<>();
    }

    public List<String> getRemovedImages() {
        return removedImages;
    }

    public void setRemovedImages(List<String> removedImages) {
        this.removedImages = removedImages;
    }
}
