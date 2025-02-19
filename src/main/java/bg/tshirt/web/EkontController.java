package bg.tshirt.web;

import bg.tshirt.database.dto.ekontDTO.EkontOfficesDTO;
import bg.tshirt.service.EkontCityService;
import bg.tshirt.service.EkontOfficesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ekont")
public class EkontController {
    private final EkontCityService ekontCityService;
    private final EkontOfficesService ekontOfficesService;

    public EkontController(EkontCityService ekontCityService,
                           EkontOfficesService ekontOfficesService) {
        this.ekontCityService = ekontCityService;
        this.ekontOfficesService = ekontOfficesService;
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities() {
        return ResponseEntity.ok(this.ekontCityService.getCities());
    }

    @GetMapping("offices/search")
    public ResponseEntity<?> searchCities(@RequestParam String name) {
        List<EkontOfficesDTO> offices = this.ekontOfficesService.getOffices(name);
        return ResponseEntity.ok(offices);
    }
}
