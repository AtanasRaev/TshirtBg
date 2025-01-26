package bg.tshirt.service;

import bg.tshirt.database.dto.ClothDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothPageDTO;

public interface ClothService {
    boolean addCloth(ClothDTO clothAddDTO);

    ClothPageDTO findById(Long id);

    boolean editCloth(ClothEditDTO clothAddDTO, Long id);
}
