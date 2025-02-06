package bg.tshirt.database.repository;

import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, Long> {
    Optional<Cloth> findByModelAndTypeAndGender(String model, Type type, Gender gender);

    @Query("SELECT c FROM Cloth c WHERE LOWER(c.name) LIKE LOWER(:query) OR LOWER(c.model) LIKE LOWER(:query)")
    Page<Cloth> findByQuery(Pageable pageable, @Param("query") String query);

    @Query("SELECT c FROM Cloth c WHERE LOWER(c.category) LIKE LOWER(:category) ORDER BY c.saleCount DESC")
    Page<Cloth> findByCategory(Pageable pageable, @Param("category") String category);

    @Query("SELECT c FROM Cloth c WHERE LOWER(c.type) LIKE LOWER(:type) ORDER BY c.saleCount DESC")
    Page<Cloth> findByType(Pageable pageable, @Param("type") String type);

    @Query("SELECT c FROM Cloth c WHERE LOWER(c.type) LIKE LOWER(:type) AND LOWER(c.category) LIKE LOWER(:category) ORDER BY c.saleCount DESC")
    Page<Cloth> findByTypeAndCategory(Pageable pageable, @Param("type") String type, @Param("category") String category);

    @Query("SELECT c FROM Cloth c ORDER BY c.id DESC")
    Page<Cloth> findAllDesc(Pageable pageable);

    @Query("SELECT c FROM Cloth c WHERE LOWER(c.type) LIKE LOWER(:type) ORDER BY c.id DESC")
    Page<Cloth> findAllWithTypeDesc(Pageable pageable, @Param("type") String type);

    @Query("SELECT c FROM Cloth c ORDER BY c.saleCount DESC")
    Page<Cloth> findAllOrderBySaleCount(Pageable pageable);
}
