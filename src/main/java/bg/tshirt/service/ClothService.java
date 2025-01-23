package bg.tshirt.service;

import bg.tshirt.database.dto.ClothAddDTO;

public interface ClothService {
    boolean addCloth(ClothAddDTO clothDTO);
}
