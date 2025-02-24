package bg.tshirt.database.repository;

import bg.tshirt.database.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    void deleteByUserEmail(String email);

    Optional<PasswordResetToken> findByToken(String token);
}
