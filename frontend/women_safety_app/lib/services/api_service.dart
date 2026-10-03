import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class ApiService {


  static const String baseUrl =
      'http://10.31.177.88:8081';




  static Future<Map<String, dynamic>> registerUser({
    required String name,
    required String email,
    required String password,
  }) async {

    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/auth/register'),

      headers: {
        'Content-Type': 'application/json',
      },

      body: jsonEncode({
        'name': name,
        'email': email,
        'password': password,
      }),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {
      return data;
    }

    throw Exception(
      data['message'] ??
          data['error'] ??
          'Registration failed',
    );
  }


 

  static Future<Map<String, dynamic>> loginUser({
    required String email,
    required String password,
  }) async {

    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/auth/login'),

      headers: {
        'Content-Type': 'application/json',
      },

      body: jsonEncode({
        'email': email,
        'password': password,
      }),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {

      final token = data['token'];

      if (token == null ||
          token.toString().isEmpty) {

        throw Exception(
          'Login succeeded but token was not returned',
        );
      }

      final prefs =
          await SharedPreferences.getInstance();

      await prefs.setString(
        'jwt_token',
        token.toString(),
      );

      return data;
    }

    throw Exception(
      data['message'] ??
          data['error'] ??
          'Login failed',
    );
  }




  static Future<String?> getToken() async {

    final prefs =
        await SharedPreferences.getInstance();

    return prefs.getString('jwt_token');
  }


 

  static Future<void> logout() async {

    final prefs =
        await SharedPreferences.getInstance();

    await prefs.remove('jwt_token');
  }




  static Future<Map<String, dynamic>> createSos({
    required String userId,
    required String deviceId,
  }) async {

    final token = await getToken();

    if (token == null || token.isEmpty) {
      throw Exception('You are not logged in');
    }

    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/device/sos'),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },

      body: jsonEncode({
        'userId': userId,
        'deviceId': deviceId,
      }),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {
      return data;
    }

    throw Exception(
      data['message'] ??
          data['error'] ??
          'SOS request failed',
    );
  }




  static Future<List<dynamic>> getTrustedContacts() async {

    final token = await getToken();

    if (token == null || token.isEmpty) {
      throw Exception('You are not logged in');
    }

    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/contacts'),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    final data = jsonDecode(response.body);

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {
      return data;
    }

    throw Exception(
      data['message'] ??
          data['error'] ??
          'Failed to load trusted contacts',
    );
  }


  

  static Future<Map<String, dynamic>> lookupUserByEmail({
    required String email,
  }) async {

    final token = await getToken();

    if (token == null || token.isEmpty) {
      throw Exception('You are not logged in');
    }

    final encodedEmail =
        Uri.encodeQueryComponent(email);

    final response = await http.get(
      Uri.parse(
        '$baseUrl/api/v1/users/lookup?email=$encodedEmail',
      ),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 404) {
      throw Exception(
        'No registered app user found with this email',
      );
    }

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {

      return jsonDecode(response.body);
    }

    String errorMessage =
        'Failed to find user';

    try {

      final data =
          jsonDecode(response.body);

      errorMessage =
          data['message'] ??
              data['error'] ??
              errorMessage;

    } catch (_) {}

    throw Exception(
      '$errorMessage '
      '(Status: ${response.statusCode})',
    );
  }



  static Future<Map<String, dynamic>> addTrustedContact({
    required String name,
    required String phone,

   
    String? contactUserId,
  }) async {

    final token = await getToken();

    if (token == null || token.isEmpty) {
      throw Exception('You are not logged in');
    }

    final Map<String, dynamic> requestBody = {
      'name': name,
      'phone': phone,
    };

    // Only send contactUserId when available.
    if (contactUserId != null &&
        contactUserId.isNotEmpty) {

      requestBody['contactUserId'] =
          contactUserId;
    }

    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/contacts'),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },

      body: jsonEncode(requestBody),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {

      return data;
    }

    throw Exception(
      data['message'] ??
          data['error'] ??
          'Failed to add trusted contact',
    );
  }




  static Future<void> deleteTrustedContact({
    required String contactId,
  }) async {

    final token = await getToken();

    if (token == null || token.isEmpty) {
      throw Exception('You are not logged in');
    }

    final response = await http.delete(
      Uri.parse(
        '$baseUrl/api/v1/contacts/$contactId',
      ),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200 ||
        response.statusCode == 204) {
      return;
    }

    String errorMessage =
        'Failed to delete contact';

    try {

      final data =
          jsonDecode(response.body);

      errorMessage =
          data['message'] ??
              data['error'] ??
              errorMessage;

    } catch (_) {}

    throw Exception(errorMessage);
  }


  

  static Future<void> saveFcmToken(
    String fcmToken,
  ) async {

    final token = await getToken();

    if (token == null || token.isEmpty) {

      print(
        'JWT token not available. '
        'FCM token will be saved after login.',
      );

      return;
    }

    final response = await http.post(
      Uri.parse(
        '$baseUrl/api/v1/notification/fcm-token',
      ),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },

      body: jsonEncode({
        'fcmToken': fcmToken,
      }),
    );

    if (response.statusCode >= 200 &&
        response.statusCode < 300) {

      print(
        'FCM token successfully registered.',
      );

      return;
    }

    String errorMessage =
        'Failed to save FCM token';

    try {

      final data =
          jsonDecode(response.body);

      errorMessage =
          data['message'] ??
              data['error'] ??
              errorMessage;

    } catch (_) {}

    throw Exception(
      '$errorMessage '
      '(Status: ${response.statusCode})',
    );
  }
}