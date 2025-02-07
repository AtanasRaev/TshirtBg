package bg.tshirt.service;

import bg.tshirt.database.dto.ClothDTO;
import bg.tshirt.database.dto.ClothDetailsPageDTO;
import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothPageDTO;
import bg.tshirt.database.entity.Order;
import bg.tshirt.database.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClothService {
    boolean addCloth(ClothDTO clothAddDTO);

    ClothDetailsPageDTO findById(Long id);

    boolean editCloth(ClothEditDTO clothAddDTO, Long id);

    Page<ClothPageDTO> findByQuery(Pageable pageable, String query);

    Page<ClothPageDTO> findByCategory(Pageable pageable, String category);

    Page<ClothPageDTO> findByType(Pageable pageable, String type);

    Page<ClothPageDTO> findByTypeAndCategory(Pageable pageable, String type, String category);

    void setTotalSales(List<OrderItem> items, String newStatus, String oldStatus);

    Page<ClothPageDTO> getNewest(Pageable pageable);

    Page<ClothPageDTO> getNewest(Pageable pageable, String type);

    Page<ClothPageDTO> getMostSold(Pageable pageable);

    boolean delete(Long id);
}
