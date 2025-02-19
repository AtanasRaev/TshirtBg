package bg.tshirt.database.dto.ekontDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EkontBodyApi {
    private String countryCode;

    @JsonProperty("cityID")
    private Long cityId;

    public EkontBodyApi(String countryCode) {
        this.countryCode = countryCode;
    }

    public EkontBodyApi(String countryCode, Long cityId) {
        this.countryCode = countryCode;
        this.cityId = cityId;
    }

    public EkontBodyApi() {
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
