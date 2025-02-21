package bg.tshirt.service;

import bg.tshirt.database.dto.econtDTO.EcontOfficesDTO;

import java.util.List;

public interface EcontOfficesService {
    List<EcontOfficesDTO> getOffices(String name);
}
