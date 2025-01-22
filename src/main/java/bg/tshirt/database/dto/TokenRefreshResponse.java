package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class TokenRefreshResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token_expiry")
    private Date accessTokenExpiry;

    @JsonProperty("refresh_token_expiry")
    private Date refreshTokenExpiry;

    public TokenRefreshResponse(String accessToken,
                                String refreshToken,
                                Date accessTokenExpiry,
                                Date refreshTokenExpiry) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(Date accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public Date getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Date refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }
}

