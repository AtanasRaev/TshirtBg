package bg.tshirt.web;

import bg.tshirt.service.EcontCityService;
import bg.tshirt.service.EcontOfficesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/econt")
public class EcontController {
    private final EcontCityService econtCityService;
    private final EcontOfficesService econtOfficesService;

    public EcontController(EcontCityService econtCityService,
                           EcontOfficesService econtOfficesService) {
        this.econtCityService = econtCityService;
        this.econtOfficesService = econtOfficesService;
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities() {
        return ResponseEntity.ok(this.econtCityService.getCities());
    }

    @GetMapping("offices/search")
    public ResponseEntity<?> searchCities(@RequestParam String name) {
        return ResponseEntity.ok(this.econtOfficesService.getOffices(name));
    }
}
