package bg.tshirt.service.impl;

import bg.tshirt.database.dto.UserDTO;
import bg.tshirt.database.entity.PasswordResetToken;
import bg.tshirt.database.repository.PasswordResetTokenRepository;
import bg.tshirt.service.EmailService;
import bg.tshirt.service.PasswordResetService;
import bg.tshirt.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${app.passwordResetExpirationInMs}")
    private long expirationInMs;

    @Value("${app.frontendUrl}")
    private String appUrl;

    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository,
                                    UserService userService,
                                    EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void createPasswordResetToken(String email) {
        UserDTO user = userService.findByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            Instant expiryDate = Instant.now().plusMillis(expirationInMs);

            PasswordResetToken resetToken = new PasswordResetToken(email, token, expiryDate);
            this.tokenRepository.deleteByUserEmail(email);
            this.tokenRepository.save(resetToken);

            String resetLink = this.appUrl + "/reset-password?token=" + token;
            this.emailService.sendPasswordResetEmail(email, resetLink);
        }
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(Instant.now());
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(Instant.now())) {
            return false;
        }

        String email = tokenOpt.get().getUserEmail();
        UserDTO user = userService.findByEmail(email);

        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        if (!this.userService.resetUserPassword(user)) {
            return false;
        }

        this.tokenRepository.delete(tokenOpt.get());
        return true;
    }
}

