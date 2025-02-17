package bg.tshirt.service;

import bg.tshirt.database.dto.ClothEditDTO;
import bg.tshirt.database.dto.ClothingDTO;
import bg.tshirt.database.dto.ClothingDetailsPageDTO;
import bg.tshirt.database.dto.ClothingPageDTO;
import bg.tshirt.database.entity.OrderItem;
import bg.tshirt.database.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ClothingService {
    boolean addClothing(ClothingDTO clothAddDTO);

    ClothingDetailsPageDTO findById(Long id);

    boolean editCloth(ClothEditDTO clothAddDTO, Long id);

    Page<ClothingPageDTO> findByQuery(Pageable pageable, String query);

    Page<ClothingPageDTO> findByQuery(Pageable pageable, String query, String type);

    Page<ClothingPageDTO> findByCategory(Pageable pageable, List<String> category);

    Page<ClothingPageDTO> findByType(Pageable pageable, String type);

    Page<ClothingPageDTO> findByTypeAndCategory(Pageable pageable, String type, List<String> category);

    void setTotalSales(List<OrderItem> items, String newStatus, String oldStatus);

    Page<ClothingPageDTO> getAllPage(Pageable pageable);

    boolean delete(Long id);

    Map<Category, Long> getClothingCountByCategories(String type);
}
