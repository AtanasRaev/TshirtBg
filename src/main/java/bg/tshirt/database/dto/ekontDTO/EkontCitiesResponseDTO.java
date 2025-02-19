package bg.tshirt.database.dto.ekontDTO;

import java.util.List;

public class EkontCitiesResponseDTO {
   private List<EkontCitiesDTO> cities;

    public List<EkontCitiesDTO> getCities() {
        return cities;
    }

    public void setCities(List<EkontCitiesDTO> cities) {
        this.cities = cities;
    }
}
