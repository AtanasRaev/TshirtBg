package bg.tshirt.service;

import bg.tshirt.database.dto.ekontDTO.EkontOfficesDTO;

import java.util.List;

public interface EkontOfficesService {
    List<EkontOfficesDTO> getOffices(String name);
}
