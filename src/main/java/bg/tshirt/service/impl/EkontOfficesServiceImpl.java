package bg.tshirt.service.impl;

import bg.tshirt.config.EkontConfig;
import bg.tshirt.database.dto.ekontDTO.*;
import bg.tshirt.service.EkontCityService;
import bg.tshirt.service.EkontOfficesService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class EkontOfficesServiceImpl implements EkontOfficesService {
    private final EkontConfig ekontConfig;
    private final RestClient restClient;
    private final EkontCityService ekontCityService;

    public EkontOfficesServiceImpl(EkontConfig ekontConfig,
                                   RestClient restClient,
                                   EkontCityService ekontCityService) {
        this.ekontConfig = ekontConfig;
        this.restClient = restClient;
        this.ekontCityService = ekontCityService;
    }

    @Override
    public List<EkontOfficesDTO> getOffices(String name) {
        String[] tokens = name.trim().split("[\\p{Punct}\\s]+");;

        List<EkontCitiesDTO> matchedCities = filterCitiesByName(tokens[0]);

        List<EkontOfficesDTO> allOffices = new ArrayList<>();
        for (EkontCitiesDTO city : matchedCities) {
            EkontOfficesResponseDTO response = fetchOfficesForCity(city);
            if (response != null && response.getOffices() != null && !response.getOffices().isEmpty()) {
                allOffices.addAll(response.getOffices());
            }
        }

        if (allOffices.isEmpty()) {
            return null;
        }

        return filterOfficesByAddress(allOffices, name, tokens);
    }

    private List<EkontCitiesDTO> filterCitiesByName(String token) {
        EkontCitiesResponseDTO cities = this.ekontCityService.getCities();
        String lowerToken = token.toLowerCase();
        return cities.getCities().stream()
                .filter(city ->
                        (city.getName() != null && city.getName().toLowerCase().contains(lowerToken)) ||
                                (city.getNameEn() != null && city.getNameEn().toLowerCase().contains(lowerToken))
                )
                .toList();
    }

    private EkontOfficesResponseDTO fetchOfficesForCity(EkontCitiesDTO city) {
        EkontBodyApi body = new EkontBodyApi(ekontConfig.getCountryCode(), city.getId());
        return this.restClient
                .post()
                .uri(this.ekontConfig.getUrlOffices())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .retrieve()
                .body(EkontOfficesResponseDTO.class);
    }

    private List<EkontOfficesDTO> filterOfficesByAddress(List<EkontOfficesDTO> offices, String originalQuery, String[] tokens) {
        String queryLower = originalQuery.trim().toLowerCase();

        return offices.stream()
                .filter(office -> {
                    String fullAddress = office.getAddress().getFullAddress();
                    String fullAddressEn = office.getAddress().getFullAddressEn();

                    String addressLower = fullAddress != null ? fullAddress.trim().toLowerCase() : "";
                    String addressEnLower = fullAddressEn != null ? fullAddressEn.trim().toLowerCase() : "";

                    if (addressLower.equals(queryLower) || addressEnLower.equals(queryLower)) {
                        return true;
                    }

                    for (String token : tokens) {
                        String tokenLower = token.toLowerCase();
                        String blok = "блок.".toLowerCase();
                        if (tokenLower.contains(blok)) {
                            String[] splitTokens = tokenLower.split("\\.");
                            for (String part : splitTokens) {
                                if (!addressLower.contains(part) && !addressEnLower.contains(part)) {
                                    return false;
                                }
                            }
                        } else {
                            if (!addressLower.contains(tokenLower) && !addressEnLower.contains(tokenLower)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }).toList();
    }
}
