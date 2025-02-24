package bg.tshirt.database.repository;

import bg.tshirt.database.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE LOWER(o.status) LIKE LOWER(:status)")
    Page<Order> findAllByStatus(Pageable pageable, @Param("status") String status);

    @Query("SELECT o FROM Order o WHERE o.user.email = :userEmail")
    Page<Order> findByUserId(@Param("userEmail") String userEmail, Pageable pageable);

    @Query("SELECT u.id FROM User u ORDER BY u.id DESC")
    Optional<Long> findLastId();
}
