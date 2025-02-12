package bg.tshirt.config;

import bg.tshirt.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private long jwtRefreshExpirationInMs;

    private Key key;

    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public JwtTokenProvider(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication, String deviceFingerprint) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .claim("type", "access")
                .claim("roles", customUserDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .claim("fingerprint", deviceFingerprint)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email, String deviceFingerprint) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtRefreshExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .claim("type", "refresh")
                .claim("fingerprint", deviceFingerprint)
                .signWith(key)
                .compact();
    }


    public Instant getExpirationDate(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().toInstant();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseToken(token);
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token, String currentFingerprint) {
        try {
            Claims claims = parseToken(token);
            String tokenHashedFingerprint = claims.get("fingerprint", String.class);
            return currentFingerprint.equals(tokenHashedFingerprint);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT token: {}", ex.getMessage());
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public String getJtiFromJwt(String token) {
        return parseToken(token).getId();
    }

    public String getEmailFromJwt(String token) {
        return parseToken(token).getSubject();
    }

    public List<?> getRolesFromJwt(String token) {
        return parseToken(token).get("roles", List.class);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isTokenTypeValid(String token, String expectedType) {
        try {
            Claims claims = parseToken(token);
            String tokenType = (String) claims.get("type");
            return expectedType.equals(tokenType);
        } catch (Exception ex) {
            logger.error("Error validating token type: {}", ex.getMessage());
            return false;
        }
    }

    public String generateDeviceFingerprint(HttpServletRequest request) {
        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("Unknown UserAgent");
        String screenResolution = Optional.ofNullable(request.getHeader("screenResolution")).orElse("Unknown Resolution");
        String timezone = Optional.ofNullable(request.getHeader("timezone")).orElse("Unknown Timezone");
        String language = Optional.ofNullable(request.getHeader("Accept-Language")).orElse("Unknown Language");
        String hardwareConcurrency = Optional.ofNullable(request.getHeader("hardwareConcurrency")).orElse("Unknown Hardware");
        String deviceMemory = Optional.ofNullable(request.getHeader("deviceMemory")).orElse("Unknown Memory");

        String fingerprint = userAgent + screenResolution + timezone + language + hardwareConcurrency + deviceMemory;
        return hashFunction(fingerprint);
    }

    public boolean isValidToken(String token, HttpServletRequest request) {
        if (token == null) {
            return false;
        }

        String currentFingerprint = generateDeviceFingerprint(request);
        return this.validateToken(token, currentFingerprint) &&
                this.isTokenTypeValid(token, "access");
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String hashFunction(String input) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");

            hmacSha256.init(secretKeySpec);

            byte[] hash = hmacSha256.doFinal(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new RuntimeException("Error computing HMAC", ex);
        }
    }
}