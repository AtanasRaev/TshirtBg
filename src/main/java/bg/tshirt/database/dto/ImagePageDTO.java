package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImagePageDTO {
    private String path;

    @JsonProperty("public_id")
    private String publicId;

    public String getSide() {
        char side = publicId.charAt(publicId.length() - 1);
        if (side == 'F') {
            return "front";
        }
        return "back";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}
