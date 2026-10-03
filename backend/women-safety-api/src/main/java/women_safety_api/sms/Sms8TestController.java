package women_safety_api.sms;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sms8")
public class Sms8TestController {

    private final Sms8Service sms8Service;

    public Sms8TestController(Sms8Service sms8Service) {
        this.sms8Service = sms8Service;
    }

    @PostMapping("/test")
    public ResponseEntity<String> testSms(
            @RequestParam String phone) {

        String message =
                "Women Safety Band test alert.\n"
                + "Spring Boot SMS8 integration is working.";

        String response =
                sms8Service.sendSms(
                        phone,
                        message
                );

        return ResponseEntity.ok(response);
    }
}