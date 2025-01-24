package bg.tshirt.database.repository;

import bg.tshirt.database.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.publicId IN :publicIds")
    List<Image> findAllByPublicIds(@Param("publicIds") List<String> publicIds);

    Optional<Image> findByPublicId(String publicId);

    Optional<Image> findByPath(String path);
}
