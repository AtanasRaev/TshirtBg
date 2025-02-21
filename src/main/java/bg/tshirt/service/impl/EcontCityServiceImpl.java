package bg.tshirt.service.impl;

import bg.tshirt.config.EcontConfig;
import bg.tshirt.database.dto.econtDTO.EcontBodyApi;
import bg.tshirt.database.dto.econtDTO.EcontCitiesResponseDTO;
import bg.tshirt.service.EcontCityService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EcontCityServiceImpl implements EcontCityService {
    private final EcontConfig econtConfig;
    private final RestClient restClient;

    public EcontCityServiceImpl(EcontConfig econtConfig,
                                RestClient restClient) {
        this.econtConfig = econtConfig;
        this.restClient = restClient;
    }

    @Override
    @Cacheable(value = "econtCities")
    public EcontCitiesResponseDTO getCities() {
        EcontBodyApi econtBodyApi = new EcontBodyApi(this.econtConfig.getCountryCode());

        return this.restClient
                .post()
                .uri(this.econtConfig.getUrlCities())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(econtBodyApi)
                .retrieve()
                .body(EcontCitiesResponseDTO.class);
    }
}
