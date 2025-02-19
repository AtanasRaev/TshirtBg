package bg.tshirt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TshirtBgApplication {
    public static void main(String[] args) {
        SpringApplication.run(TshirtBgApplication.class, args);
    }
}
