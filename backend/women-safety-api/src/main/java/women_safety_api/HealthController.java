package women_safety_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public String healthCheck() {
        return "Women Safety Band Backend is running";
    }
}