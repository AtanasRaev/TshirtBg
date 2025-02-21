package bg.tshirt.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheScheduler {
    @CacheEvict(value = "econtCities", allEntries = true)
    @Scheduled(fixedRate = 5 * 24 * 60 * 60 * 1000)
    public void clearEcontCitiesCache() {
    }
}

