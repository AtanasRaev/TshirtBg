package bg.tshirt.database.dto;

import java.util.ArrayList;
import java.util.List;

public class ClothEditDTO extends ClothingDTO {
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
