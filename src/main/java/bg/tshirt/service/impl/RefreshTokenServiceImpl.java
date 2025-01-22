package bg.tshirt.service.impl;

import bg.tshirt.database.entity.RefreshToken;
import bg.tshirt.database.repository.RefreshTokenRepository;
import bg.tshirt.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken saveNewToken(String tokenId, String userEmail, Date expiryDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(tokenId);
        refreshToken.setUserEmail(userEmail);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean isValid(String tokenId) {
        return refreshTokenRepository.findByTokenId(tokenId)
                .filter(token -> !token.isRevoked() && token.getExpiryDate().after(new Date()))
                .isPresent();
    }

    @Override
    public void revokeToken(String tokenId) {
        refreshTokenRepository.findByTokenId(tokenId).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Scheduled(cron = "0 * * * *")
    private void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(new Date());
    }
}

