package women_safety_api.notification;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmNotificationService {

    public String sendEmergencyNotification(
            String fcmToken,
            String title,
            String body) {

        try {

            Notification notification =
                    Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build();

            Message message =
                    Message.builder()
                            .setToken(fcmToken)
                            .setNotification(notification)
                            .build();

            String response =
                    FirebaseMessaging
                            .getInstance()
                            .send(message);

            System.out.println(
                    "FCM notification sent successfully. Message ID: "
                            + response
            );

            return response;

        } catch (Exception e) {

            System.out.println(
                    "FCM notification failed: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Failed to send FCM notification",
                    e
            );
        }
    }
}