package bg.tshirt.service;

import bg.tshirt.database.entity.RefreshToken;

import java.util.Date;

public interface RefreshTokenService {
    RefreshToken saveNewToken(String tokenId, String userEmail, Date expiryDate);

    boolean isValid(String tokenId);

    void revokeToken(String tokenId);

}
