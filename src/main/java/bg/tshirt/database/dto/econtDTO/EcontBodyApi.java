package bg.tshirt.database.dto.econtDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EcontBodyApi {
    private String countryCode;

    @JsonProperty("cityID")
    private Long cityId;

    public EcontBodyApi(String countryCode) {
        this.countryCode = countryCode;
    }

    public EcontBodyApi(String countryCode, Long cityId) {
        this.countryCode = countryCode;
        this.cityId = cityId;
    }

    public EcontBodyApi() {
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}
