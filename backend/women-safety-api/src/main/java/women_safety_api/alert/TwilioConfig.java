package women_safety_api.alert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.twilio.Twilio;

@Configuration
public class TwilioConfig {

    @Bean
    public String initializeTwilio() {

        String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
        String authToken = System.getenv("TWILIO_AUTH_TOKEN");

        if (accountSid == null || accountSid.isBlank()) {
            throw new IllegalStateException(
                    "TWILIO_ACCOUNT_SID environment variable is missing"
            );
        }

        if (authToken == null || authToken.isBlank()) {
            throw new IllegalStateException(
                    "TWILIO_AUTH_TOKEN environment variable is missing"
            );
        }

        Twilio.init(accountSid, authToken);

        return "Twilio initialized";
    }
}