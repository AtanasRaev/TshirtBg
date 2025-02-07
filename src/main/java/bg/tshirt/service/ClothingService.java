package bg.tshirt.service;

import bg.tshirt.database.dto.ClothingDTO;
import bg.tshirt.database.dto.ClothingDetailsPageDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothingPageDTO;
import bg.tshirt.database.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClothingService {
    boolean addCloth(ClothingDTO clothAddDTO);

    ClothingDetailsPageDTO findById(Long id);

    boolean editCloth(ClothEditDTO clothAddDTO, Long id);

    Page<ClothingPageDTO> findByQuery(Pageable pageable, String query);

    Page<ClothingPageDTO> findByCategory(Pageable pageable, String category);

    Page<ClothingPageDTO> findByType(Pageable pageable, String type);

    Page<ClothingPageDTO> findByTypeAndCategory(Pageable pageable, String type, String category);

    void setTotalSales(List<OrderItem> items, String newStatus, String oldStatus);

    Page<ClothingPageDTO> getNewest(Pageable pageable);

    Page<ClothingPageDTO> getNewest(Pageable pageable, String type);

    Page<ClothingPageDTO> getMostSold(Pageable pageable);

    boolean delete(Long id);
}
