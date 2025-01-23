package bg.tshirt.database.repository;

import bg.tshirt.database.entity.Cloth;
import bg.tshirt.database.entity.enums.Gender;
import bg.tshirt.database.entity.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, Integer> {
    Optional<Cloth> findByModelAndTypeAndGender(String model, Type type, Gender gender);
}
