package bg.tshirt.service.impl;

import bg.tshirt.config.EkontConfig;
import bg.tshirt.database.dto.ekontDTO.EkontBodyApi;
import bg.tshirt.database.dto.ekontDTO.EkontCitiesResponseDTO;
import bg.tshirt.service.EkontCityService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EkontCityServiceImpl implements EkontCityService {
    private final EkontConfig ekontConfig;
    private final RestClient restClient;

    public EkontCityServiceImpl(EkontConfig ekontConfig,
                                RestClient restClient) {
        this.ekontConfig = ekontConfig;
        this.restClient = restClient;
    }

    @Override
    @Cacheable(value = "ekontCities")
    public EkontCitiesResponseDTO getCities() {
        EkontBodyApi ekontBodyApi = new EkontBodyApi(this.ekontConfig.getCountryCode());

        return this.restClient
                .post()
                .uri(this.ekontConfig.getUrlCities())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(ekontBodyApi)
                .retrieve()
                .body(EkontCitiesResponseDTO.class);
    }
}
