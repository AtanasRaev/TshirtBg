package bg.tshirt.service;

import bg.tshirt.database.entity.RefreshToken;

import java.time.Instant;
import java.util.Date;

public interface RefreshTokenService {
    void saveNewToken(String tokenId, String userEmail, Instant expiryDate);

    boolean isValid(String tokenId);

    void revokeToken(String tokenId);

}
