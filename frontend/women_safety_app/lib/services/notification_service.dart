import 'package:firebase_messaging/firebase_messaging.dart';
import 'api_service.dart';

class NotificationService {
  static final FirebaseMessaging _messaging =
      FirebaseMessaging.instance;

  // ============================================================
  // INITIALIZE FCM
  // ============================================================

  static Future<void> initialize() async {
    // ----------------------------------------------------------
    // REQUEST NOTIFICATION PERMISSION
    // ----------------------------------------------------------

    NotificationSettings settings =
        await _messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
    );

    print(
      'Notification permission: '
      '${settings.authorizationStatus}',
    );

    // ----------------------------------------------------------
    // GET FCM TOKEN
    // ----------------------------------------------------------

    String? token = await _messaging.getToken();

    print('======================================');
    print('FCM TOKEN:');
    print(token);
    print('======================================');

    // ----------------------------------------------------------
    // TRY TO SAVE TOKEN
    // ----------------------------------------------------------
    //
    // At application startup the user may not be logged in yet.
    // saveTokenToBackend() safely handles this situation.
    //

    if (token != null && token.isNotEmpty) {
      await saveTokenToBackend(token);
    }

    // ----------------------------------------------------------
    // TOKEN REFRESH
    // ----------------------------------------------------------

    _messaging.onTokenRefresh.listen(
      (newToken) async {
        print('======================================');
        print('FCM TOKEN UPDATED:');
        print(newToken);
        print('======================================');

        await saveTokenToBackend(newToken);
      },
    );

    // ----------------------------------------------------------
    // FOREGROUND MESSAGE
    // ----------------------------------------------------------

    FirebaseMessaging.onMessage.listen(
      (RemoteMessage message) {
        print('======================================');
        print('FCM MESSAGE RECEIVED');

        print(
          'Title: '
          '${message.notification?.title}',
        );

        print(
          'Body: '
          '${message.notification?.body}',
        );

        print(
          'Data: '
          '${message.data}',
        );

        print('======================================');
      },
    );
  }

  // ============================================================
  // GET CURRENT FCM TOKEN
  // ============================================================

  static Future<String?> getCurrentToken() async {
    return await _messaging.getToken();
  }

  // ============================================================
  // SAVE FCM TOKEN TO BACKEND
  // ============================================================

  static Future<void> saveTokenToBackend(
      String token) async {
    try {
      await ApiService.saveFcmToken(token);

      print(
        'FCM token saved to backend successfully.',
      );
    } catch (e) {
      print(
        'Failed to save FCM token to backend: $e',
      );
    }
  }
}