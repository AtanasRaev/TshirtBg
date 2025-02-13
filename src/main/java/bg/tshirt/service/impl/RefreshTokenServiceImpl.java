package bg.tshirt.service.impl;

import bg.tshirt.database.entity.RefreshToken;
import bg.tshirt.database.repository.RefreshTokenRepository;
import bg.tshirt.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void saveNewToken(String tokenId, String userEmail, Instant expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(tokenId);
        refreshToken.setUserEmail(userEmail);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setRevoked(false);
        this.refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean isValid(String tokenId) {
        return this.refreshTokenRepository.findByTokenId(tokenId)
                .filter(token -> !token.isRevoked() && token.getExpiryDate().isAfter(Instant.now()))
                .isPresent();
    }

    @Override
    public void revokeToken(String tokenId) {
        this.refreshTokenRepository.findByTokenId(tokenId).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Scheduled(cron = "0 * * * *")
    private void cleanupExpiredTokens() {
        this.refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
