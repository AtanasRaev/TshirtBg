package bg.tshirt.service;

import bg.tshirt.database.dto.ClothDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothDetailsPageDTO;
import bg.tshirt.database.dto.ClothPageDTO;

import java.util.List;

public interface ClothService {
    boolean addCloth(ClothDTO clothAddDTO);

    ClothDetailsPageDTO findById(Long id);

    boolean editCloth(ClothEditDTO clothAddDTO, Long id);

    List<ClothPageDTO> findByQuery(String query);

    List<ClothPageDTO> findByCategory(String query);
}
