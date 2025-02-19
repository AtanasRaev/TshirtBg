package bg.tshirt.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheScheduler {
    @CacheEvict(value = "ekontCities", allEntries = true)
    @Scheduled(fixedRate = 1_800_000)
    public void clearEkontCitiesCache() {
    }
}

