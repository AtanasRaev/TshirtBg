package bg.tshirt.service.impl;

import bg.tshirt.config.EcontConfig;
import bg.tshirt.database.dto.econtDTO.*;
import bg.tshirt.service.EcontCityService;
import bg.tshirt.service.EcontOfficesService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class EcontOfficesServiceImpl implements EcontOfficesService {
    private final EcontConfig econtConfig;
    private final RestClient restClient;
    private final EcontCityService econtCityService;

    public EcontOfficesServiceImpl(EcontConfig econtConfig,
                                   RestClient restClient,
                                   EcontCityService econtCityService) {
        this.econtConfig = econtConfig;
        this.restClient = restClient;
        this.econtCityService = econtCityService;
    }

    @Override
    public List<EcontOfficesDTO> getOffices(String name) {
        String[] tokens = name.trim().split("[\\p{Punct}\\s]+");;

        List<EcontCitiesDTO> matchedCities = filterCitiesByName(tokens[0]);

        Map<EcontCitiesDTO, List<EcontOfficesDTO>> allOffices = new HashMap<>();
        for (EcontCitiesDTO city : matchedCities) {
            EcontOfficesResponseDTO response = fetchOfficesForCity(city);
            if (response != null && response.getOffices() != null && !response.getOffices().isEmpty()) {
                allOffices.put(city, response.getOffices());
            }
        }

        if (allOffices.isEmpty()) {
            return null;
        }

        return filterOfficesByAddress(allOffices, name, tokens);
    }

    private List<EcontCitiesDTO> filterCitiesByName(String token) {
        EcontCitiesResponseDTO cities = this.econtCityService.getCities();
        String lowerToken = token.toLowerCase();
        return cities.getCities().stream()
                .filter(city ->
                        (city.getName() != null && city.getName().toLowerCase().contains(lowerToken)) ||
                                (city.getNameEn() != null && city.getNameEn().toLowerCase().contains(lowerToken))
                )
                .toList();
    }

    private EcontOfficesResponseDTO fetchOfficesForCity(EcontCitiesDTO city) {
        EcontBodyApi body = new EcontBodyApi(econtConfig.getCountryCode(), city.getId());
        return this.restClient
                .post()
                .uri(this.econtConfig.getUrlOffices())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .retrieve()
                .body(EcontOfficesResponseDTO.class);
    }

    private List<EcontOfficesDTO> filterOfficesByAddress(Map<EcontCitiesDTO, List<EcontOfficesDTO>> officesMap, String originalQuery, String[] tokens) {
        String queryLower = originalQuery.trim().toLowerCase();
        List<EcontOfficesDTO> filteredOffices = new ArrayList<>();
        officesMap.forEach((city, offices) -> {
            List<EcontOfficesDTO> list = offices.stream()
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
                            String jk = "ж.к".toLowerCase();
                            if (tokenLower.contains(blok) || tokenLower.contains(jk)) {
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
                        String[] split = office.getAddress().getFullAddress().trim().split(city.getName());
                        office.getAddress().setFullAddress(city.getName() + " (" + city.getRegionName() + ") " + split[1].trim());
                        return true;
                    }).toList();
            filteredOffices.addAll(list);
        });
        return filteredOffices;
    }
}
