package bg.tshirt.database.repository;

import bg.tshirt.database.entity.Clothing;
import bg.tshirt.database.entity.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingRepository extends JpaRepository<Clothing, Long> {
    Optional<Clothing> findByModelAndType(String model, Type type);

    @Query("SELECT c FROM Clothing c WHERE LOWER(c.name) LIKE LOWER(:query) OR LOWER(c.model) LIKE LOWER(:query)")
    Page<Clothing> findByQuery(Pageable pageable, @Param("query") String query);

    @Query("SELECT c FROM Clothing c WHERE (LOWER(c.name) LIKE LOWER(:query) OR LOWER(c.model) LIKE LOWER(:query)) AND LOWER(c.type) IN (:type)")
    Page<Clothing> findByQueryAndType(Pageable pageable, @Param("query") String query, @Param("type") List<String> type);

    @Query("SELECT c FROM Clothing c WHERE LOWER(c.category) IN (:category)")
    Page<Clothing> findByCategory(Pageable pageable, @Param("category") List<String> category);

    @Query("SELECT c FROM Clothing c WHERE LOWER(c.type) LIKE LOWER(:type)")
    Page<Clothing> findByType(Pageable pageable, @Param("type") String type);

    @Query("SELECT c FROM Clothing c WHERE LOWER(c.type) LIKE LOWER(:type) AND LOWER(c.category) IN (:category)")
    Page<Clothing> findByTypeAndCategory(Pageable pageable, @Param("type") String type, @Param("category") List<String> category);

    @Query("SELECT c FROM Clothing c")
    Page<Clothing> findAllPage(Pageable pageable);

    Optional<Clothing> findByModel(String model);

    @Query("SELECT c.category, COUNT(c) FROM Clothing c GROUP BY c.category")
    List<Object[]> countClothingByCategory();

    @Query("SELECT c.category, COUNT(c) FROM Clothing c WHERE LOWER(c.type) LIKE LOWER(:type) GROUP BY c.category")
    List<Object[]> countClothingByCategory(String type);
}
