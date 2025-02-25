package bg.tshirt.web;

import bg.tshirt.config.JwtTokenProvider;
import bg.tshirt.database.dto.TokenRefreshResponse;
import bg.tshirt.service.PasswordResetService;
import bg.tshirt.service.impl.CustomUserDetailsService;
import bg.tshirt.service.impl.RefreshTokenServiceImpl;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenServiceImpl refreshTokenService;

    private final Map<String, Bucket> rateLimitBuckets = new ConcurrentHashMap<>();

    public AuthController(JwtTokenProvider jwtTokenProvider,
                          CustomUserDetailsService customUserDetailsService,
                          RefreshTokenServiceImpl refreshTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest httpRequest) {
        if (isRateLimitExceeded(httpRequest)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many requests. Please try again later."));
        }

        String refreshToken = httpRequest.getHeader("Refresh-Token");
        if (!StringUtils.hasText(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Refresh token is missing"));
        }

        try {
            if (!isRefreshTokenValid(refreshToken, httpRequest)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid refresh token"));
            }

            String email = jwtTokenProvider.getEmailFromJwt(refreshToken);
            String jti = jwtTokenProvider.getJtiFromJwt(refreshToken);

            if (!refreshTokenService.isValid(jti)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Refresh token is no longer valid"));
            }

            if (isRefreshTokenAboutToExpire(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Refresh token is about to expire"));
            }

            refreshTokenService.revokeToken(jti);

            TokenRefreshResponse tokenResponse = generateNewTokens(email, httpRequest);

            refreshTokenService.saveNewToken(
                    jwtTokenProvider.getJtiFromJwt(tokenResponse.getRefreshToken()),
                    email,
                    tokenResponse.getRefreshTokenExpiry()
            );

            return ResponseEntity.ok(tokenResponse);
        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token has expired"));
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid refresh token"));
        }
    }
    private boolean isRateLimitExceeded(HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = resolveBucket(clientIp);
        return !bucket.tryConsume(1);
    }

    private boolean isRefreshTokenValid(String refreshToken, HttpServletRequest httpRequest) {
        String currentFingerprint = jwtTokenProvider.generateDeviceFingerprint(httpRequest);
        return jwtTokenProvider.validateToken(refreshToken, currentFingerprint) && jwtTokenProvider.isTokenTypeValid(refreshToken, "refresh");
    }

    private boolean isRefreshTokenAboutToExpire(String refreshToken) {
        Instant expiryDate = jwtTokenProvider.getExpirationDate(refreshToken);
        Duration gracePeriod = Duration.ofMinutes(5);
        return expiryDate.isBefore(Instant.now().plus(gracePeriod));
    }

    private TokenRefreshResponse generateNewTokens(String email, HttpServletRequest httpRequest) {
        String currentFingerprint = jwtTokenProvider.generateDeviceFingerprint(httpRequest);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, currentFingerprint);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email, currentFingerprint);

        Instant accessTokenExpiry = jwtTokenProvider.getExpirationDate(newAccessToken);
        Instant refreshTokenExpiry = jwtTokenProvider.getExpirationDate(newRefreshToken);

        return new TokenRefreshResponse(newAccessToken, newRefreshToken, accessTokenExpiry, refreshTokenExpiry);
    }

    private Bucket resolveBucket(String clientIp) {
        return rateLimitBuckets.computeIfAbsent(clientIp, key -> {
            Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });
    }
}