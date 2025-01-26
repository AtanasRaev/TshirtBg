package bg.tshirt.database.repository;

import bg.tshirt.database.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenId(String tokenId);

    List<RefreshToken> findByUserEmail(String email);

    void deleteByExpiryDateBefore(Instant date);
}

