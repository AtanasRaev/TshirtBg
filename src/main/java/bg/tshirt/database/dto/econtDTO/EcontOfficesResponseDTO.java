package bg.tshirt.database.dto.econtDTO;

import java.util.ArrayList;
import java.util.List;

public class EcontOfficesResponseDTO {
    private List<EcontOfficesDTO> offices;

    public EcontOfficesResponseDTO() {
        this.offices = new ArrayList<>();
    }

    public List<EcontOfficesDTO> getOffices() {
        return offices;
    }

    public void setOffices(List<EcontOfficesDTO> offices) {
        this.offices = offices;
    }
}
