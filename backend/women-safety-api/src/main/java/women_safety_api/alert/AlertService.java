package women_safety_api.alert;

import java.util.List;

import org.springframework.stereotype.Service;

import women_safety_api.contact.TrustedContact;
import women_safety_api.contact.TrustedContactService;
import women_safety_api.location.LocationData;
import women_safety_api.notification.FcmNotificationService;
import women_safety_api.sms.Sms8Service;

@Service
public class AlertService {

    private final TrustedContactService trustedContactService;
    private final Sms8Service sms8Service;
    private final FcmNotificationService fcmNotificationService;

    public AlertService(
            TrustedContactService trustedContactService,
            Sms8Service sms8Service,
            FcmNotificationService fcmNotificationService) {

        this.trustedContactService = trustedContactService;
        this.sms8Service = sms8Service;
        this.fcmNotificationService = fcmNotificationService;
    }

    public void sendEmergencyAlert(
            String userId,
            String deviceId,
            LocationData location) {

        List<TrustedContact> contacts =
                trustedContactService.getContacts(userId);

        if (contacts == null || contacts.isEmpty()) {

            System.out.println(
                    "No trusted contacts found for user: "
                            + userId);

            return;
        }

        String message =
                buildEmergencyMessage(deviceId, location);

        int fcmSuccessCount = 0;
        int smsSuccessCount = 0;
        int failureCount = 0;

        for (TrustedContact contact : contacts) {

            System.out.println(
                    "Processing emergency alert for contact: "
                            + contact.getName());

            /*
             * ------------------------------------------------
             * OPTION 1: FCM
             * ------------------------------------------------
             *
             * If the trusted contact has the mobile app
             * and an FCM token, send push notification.
             */

            if (contact.getFcmToken() != null
                    && !contact.getFcmToken().isBlank()) {

                try {

                    String title =
                            "🚨 WOMEN SAFETY EMERGENCY";

                    String body =
                            "SOS triggered. Tap to view emergency location.";

                    fcmNotificationService
                            .sendEmergencyNotification(
                                    contact.getFcmToken(),
                                    title,
                                    body);

                    fcmSuccessCount++;

                    System.out.println(
                            "FCM emergency alert sent to "
                                    + contact.getName());

                    /*
                     * FCM succeeded.
                     *
                     * We do not send SMS here because this
                     * contact already has app notification.
                     */

                    continue;

                } catch (Exception e) {

                    System.out.println(
                            "FCM failed for "
                                    + contact.getName()
                                    + " | Trying SMS8 fallback.");

                    /*
                     * If FCM fails, continue to SMS8.
                     */
                }
            }

            /*
             * ------------------------------------------------
             * OPTION 2: SMS8
             * ------------------------------------------------
             *
             * Used when:
             *
             * 1. Contact has no FCM token
             * OR
             * 2. FCM notification failed
             */

            if (contact.getPhone() == null
                    || contact.getPhone().isBlank()) {

                System.out.println(
                        "Skipping SMS because phone number is missing for "
                                + contact.getName());

                failureCount++;

                continue;
            }

            try {

                String response =
                        sms8Service.sendSms(
                                contact.getPhone(),
                                message);

                smsSuccessCount++;

                System.out.println(
                        "Emergency SMS submitted to "
                                + contact.getName());

                System.out.println(
                        "SMS8 response: "
                                + response);

            } catch (Exception e) {

                failureCount++;

                System.out.println(
                        "Emergency SMS failed for "
                                + contact.getName()
                                + " | reason: "
                                + e.getMessage());
            }
        }

        System.out.println(
                "======================================");

        System.out.println(
                "Emergency alert completed");

        System.out.println(
                "FCM sent: "
                        + fcmSuccessCount);

        System.out.println(
                "SMS sent: "
                        + smsSuccessCount);

        System.out.println(
                "Failed: "
                        + failureCount);

        System.out.println(
                "======================================");
    }

    private String buildEmergencyMessage(
            String deviceId,
            LocationData location) {

        StringBuilder message =
                new StringBuilder();

        message.append(
                "WOMEN SAFETY EMERGENCY ALERT\n\n");

        message.append(
                "SOS has been triggered.\n\n");

        message.append("Device: ");
        message.append(deviceId);
        message.append("\n");

        if (location != null) {

            message.append("Location:\n");

            message.append(
                    "https://maps.google.com/?q=");

            message.append(
                    location.getLatitude());

            message.append(",");

            message.append(
                    location.getLongitude());

            message.append("\n");

            if (location.getAccuracy() != null) {

                message.append("Accuracy: ");

                message.append(
                        location.getAccuracy());

                message.append(
                        " meters\n");
            }

        } else {

            message.append(
                    "Location: Not available\n");
        }

        message.append(
                "\nPlease contact the user immediately.");

        return message.toString();
    }
}