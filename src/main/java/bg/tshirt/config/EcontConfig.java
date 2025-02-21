package bg.tshirt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcontConfig {
    @Value("${econt.api.urlCities}")
    private String urlCities;

    @Value("${econt.api.urlOffices}")
    private String urlOffices;

    public String getUrlCities() {
        return urlCities;
    }

    public void setUrlCities(String urlCities) {
        this.urlCities = urlCities;
    }

    public String getUrlOffices() {
        return urlOffices;
    }

    public void setUrlOffices(String urlOffices) {
        this.urlOffices = urlOffices;
    }

    public String getCountryCode() {
        return "BGR";
    }
}
