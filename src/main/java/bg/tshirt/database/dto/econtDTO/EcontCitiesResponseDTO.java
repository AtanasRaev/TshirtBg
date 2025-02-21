package bg.tshirt.database.dto.econtDTO;

import java.util.List;

public class EcontCitiesResponseDTO {
   private List<EcontCitiesDTO> cities;

    public List<EcontCitiesDTO> getCities() {
        return cities;
    }

    public void setCities(List<EcontCitiesDTO> cities) {
        this.cities = cities;
    }
}
