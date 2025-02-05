package bg.tshirt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "https://clothing-website-1.web.app",
                "http://localhost:*"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "screenResolution",
                "timezone",
                "hardwareConcurrency",
                "deviceMemory",
                "Accept",
                "User-Agent",
                "Accept-Language",
                "Refresh-Token"
        ));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of(
                "screenResolution",
                "timezone",
                "hardwareConcurrency",
                "deviceMemory",
                "Authorization",
                "Content-Type",
                "Accept",
                "User-Agent",
                "Accept-Language",
                "Refresh-Token"
        ));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}