package bg.tshirt.database.dto.ekontDTO;

import java.util.ArrayList;
import java.util.List;

public class EkontOfficesResponseDTO {
    private List<EkontOfficesDTO> offices;

    public EkontOfficesResponseDTO() {
        this.offices = new ArrayList<>();
    }

    public List<EkontOfficesDTO> getOffices() {
        return offices;
    }

    public void setOffices(List<EkontOfficesDTO> offices) {
        this.offices = offices;
    }
}
