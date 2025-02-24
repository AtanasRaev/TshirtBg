package bg.tshirt.service;

import java.time.Instant;

public interface RefreshTokenService {
    void saveNewToken(String tokenId, String userEmail, Instant expiryDate);

    boolean isValid(String tokenId);

    void revokeToken(String tokenId);

}
