import 'dart:async';

import 'package:flutter/material.dart';

import '../services/api_service.dart';
import 'trusted_contacts_screen.dart';

class DashboardScreen extends StatefulWidget {
  final String userName;

  const DashboardScreen({
    super.key,
    this.userName = 'User',
  });

  @override
  State<DashboardScreen> createState() =>
      _DashboardScreenState();
}

class _DashboardScreenState
    extends State<DashboardScreen> {

  // ============================================================
  // SOS
  // ============================================================

  Timer? _sosTimer;

  double _sosProgress = 0.0;

  bool _sendingSos = false;

  // Current test user/device
  static const String userId =
      '21f9c053-e9ea-4905-8bb4-1cf852e04814';

  static const String deviceId =
      'band-001';

  // ============================================================
  // START SOS HOLD
  // ============================================================

  void _startSosHold() {
    if (_sendingSos) return;

    _sosTimer?.cancel();

    setState(() {
      _sosProgress = 0.0;
    });

    const totalMilliseconds = 5000;
    const intervalMilliseconds = 50;

    int elapsed = 0;

    _sosTimer = Timer.periodic(
      const Duration(
        milliseconds: intervalMilliseconds,
      ),
      (timer) async {
        elapsed += intervalMilliseconds;

        if (!mounted) {
          timer.cancel();
          return;
        }

        setState(() {
          _sosProgress =
              elapsed / totalMilliseconds;
        });

        if (elapsed >= totalMilliseconds) {
          timer.cancel();

          await _sendSos();
        }
      },
    );
  }

  // ============================================================
  // CANCEL SOS HOLD
  // ============================================================

  void _cancelSosHold() {
    if (_sendingSos) return;

    _sosTimer?.cancel();

    if (!mounted) return;

    setState(() {
      _sosProgress = 0.0;
    });
  }

  // ============================================================
  // SEND SOS
  // ============================================================

  Future<void> _sendSos() async {
    if (_sendingSos) return;

    setState(() {
      _sendingSos = true;
      _sosProgress = 1.0;
    });

    try {
      await ApiService.createSos(
        userId: userId,
        deviceId: deviceId,
      );

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            '🚨 SOS sent successfully!',
          ),
          backgroundColor: Colors.red,
        ),
      );
    } catch (e) {
      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'SOS failed: ${e.toString().replaceFirst(
                  'Exception: ',
                  '',
                )}',
          ),
        ),
      );
    } finally {
      if (!mounted) return;

      setState(() {
        _sendingSos = false;
        _sosProgress = 0.0;
      });
    }
  }

  // ============================================================
  // DISPOSE
  // ============================================================

  @override
  void dispose() {
    _sosTimer?.cancel();

    super.dispose();
  }

  // ============================================================
  // BUILD
  // ============================================================

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,

      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding:
                const EdgeInsets.symmetric(
              horizontal: 16,
            ),

            child: Column(
              children: [

                // =================================================
                // HEADER
                // =================================================

                const SizedBox(
                  height: 20,
                ),

                Align(
                  alignment:
                      Alignment.centerLeft,

                  child: Column(
                    crossAxisAlignment:
                        CrossAxisAlignment.start,

                    children: [

                      const Text(
                        'Hello,',

                        style: TextStyle(
                          color:
                              Colors.black,

                          fontSize: 24,

                          fontWeight:
                              FontWeight.w400,
                        ),
                      ),

                      Text(
                        widget.userName,

                        style:
                            const TextStyle(
                          color:
                              Colors.black,

                          fontSize: 24,

                          fontWeight:
                              FontWeight.bold,
                        ),
                      ),

                      const Text(
                        'Stay Safe,Stay Strong',

                        style: TextStyle(
                          color:
                              Colors.grey,

                          fontSize: 16,
                        ),
                      ),
                    ],
                  ),
                ),

                // =================================================
                // SOS BUTTON
                // =================================================

                const SizedBox(
                  height: 15,
                ),

                GestureDetector(
                  onTapDown: (_) {
                    _startSosHold();
                  },

                  onTapUp: (_) {
                    if (!_sendingSos &&
                        _sosProgress < 1.0) {
                      _cancelSosHold();
                    }
                  },

                  onTapCancel: () {
                    _cancelSosHold();
                  },

                  child:
                      _buildSosButton(),
                ),

                // =================================================
                // SOS MESSAGE
                // =================================================

                const SizedBox(
                  height: 8,
                ),

                Text(
                  _sendingSos
                      ? 'Sending emergency alert...'
                      : 'Press and Hold for 5 seconds\n'
                          'to send emergency alert',

                  textAlign:
                      TextAlign.center,

                  style: const TextStyle(
                    color:
                        Color(0xFF9E9E9E),

                    fontSize: 16,

                    height: 1.25,
                  ),
                ),

                // =================================================
                // QUICK ACTIONS
                // =================================================

                const SizedBox(
                  height: 68,
                ),

                Row(
                  children: [

                    // ---------------------------------------------
                    // TRUSTED CONTACTS
                    // ---------------------------------------------

                    Expanded(
                      child:
                          _buildActionCard(
                        icon:
                            Icons.person,

                        iconColor:
                            const Color(
                          0xFF0566D6,
                        ),

                        title:
                            'Trusted Contacts',

                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder:
                                  (context) =>
                                      const TrustedContactsScreen(),
                            ),
                          );
                        },
                      ),
                    ),

                    const SizedBox(
                      width: 14,
                    ),

                    // ---------------------------------------------
                    // LIVE LOCATION
                    // ---------------------------------------------

                    Expanded(
                      child:
                          _buildActionCard(
                        icon:
                            Icons.location_on,

                        iconColor:
                            const Color(
                          0xFFFF3030,
                        ),

                        title:
                            'Live Location',

                        onTap: () {
                          _showComingSoon(
                            'Live Location',
                          );
                        },
                      ),
                    ),
                  ],
                ),

                const SizedBox(
                  height: 14,
                ),

                Row(
                  children: [

                    // ---------------------------------------------
                    // BRACELET
                    // ---------------------------------------------

                    Expanded(
                      child:
                          _buildBraceletCard(),
                    ),

                    const SizedBox(
                      width: 14,
                    ),

                    // ---------------------------------------------
                    // PROFILE
                    // ---------------------------------------------

                    Expanded(
                      child:
                          _buildActionCard(
                        icon:
                            Icons.person,

                        iconColor:
                            const Color(
                          0xFF1675D1,
                        ),

                        title:
                            'My Profile',

                        onTap: () {
                          _showComingSoon(
                            'My Profile',
                          );
                        },
                      ),
                    ),
                  ],
                ),

                const SizedBox(
                  height: 30,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  // ============================================================
  // SOS BUTTON
  // ============================================================

  Widget _buildSosButton() {
    return SizedBox(
      width: 230,
      height: 230,

      child: Stack(
        alignment:
            Alignment.center,

        children: [

          // -----------------------------------------------
          // OUTER LIGHT RED
          // -----------------------------------------------

          Container(
            width: 224,
            height: 224,

            decoration:
                const BoxDecoration(
              shape:
                  BoxShape.circle,

              color:
                  Color(0xFFFFD1D4),
            ),
          ),

          // -----------------------------------------------
          // SECOND RED
          // -----------------------------------------------

          Container(
            width: 194,
            height: 194,

            decoration:
                const BoxDecoration(
              shape:
                  BoxShape.circle,

              color:
                  Color(0xFFFF7D83),
            ),
          ),

          // -----------------------------------------------
          // THIRD RED
          // -----------------------------------------------

          Container(
            width: 166,
            height: 166,

            decoration:
                const BoxDecoration(
              shape:
                  BoxShape.circle,

              color:
                  Color(0xFFFF252D),
            ),
          ),

          // -----------------------------------------------
          // INNER RED
          // -----------------------------------------------

          Container(
            width: 140,
            height: 140,

            decoration:
                const BoxDecoration(
              shape:
                  BoxShape.circle,

              color:
                  Color(0xFFFF1018),

              boxShadow: [
                BoxShadow(
                  color:
                      Color(0x44FF0000),

                  blurRadius: 8,

                  spreadRadius: 2,
                ),
              ],
            ),

            child: Center(
              child: _sendingSos
                  ? const SizedBox(
                      width: 32,
                      height: 32,

                      child:
                          CircularProgressIndicator(
                        strokeWidth: 3,

                        color:
                            Colors.white,
                      ),
                    )
                  : const Text(
                      'SOS',

                      style:
                          TextStyle(
                        color:
                            Colors.white,

                        fontSize: 20,

                        fontWeight:
                            FontWeight.bold,
                      ),
                    ),
            ),
          ),

          // -----------------------------------------------
          // HOLD PROGRESS
          // -----------------------------------------------

          if (_sosProgress > 0 &&
              !_sendingSos)
            SizedBox(
              width: 224,
              height: 224,

              child:
                  CircularProgressIndicator(
                value:
                    _sosProgress,

                strokeWidth: 4,

                backgroundColor:
                    Colors.transparent,

                color:
                    Colors.white,
              ),
            ),
        ],
      ),
    );
  }

  // ============================================================
  // ACTION CARD
  // ============================================================

  Widget _buildActionCard({
    required IconData icon,
    required Color iconColor,
    required String title,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,

      child: Container(
        height: 102,

        decoration:
            BoxDecoration(
          color:
              Colors.white,

          borderRadius:
              BorderRadius.circular(
            16,
          ),

          boxShadow: const [
            BoxShadow(
              color:
                  Color(0x22000000),

              blurRadius: 8,

              offset:
                  Offset(0, 3),
            ),
          ],
        ),

        child: Column(
          mainAxisAlignment:
              MainAxisAlignment.center,

          children: [

            Icon(
              icon,

              size: 40,

              color:
                  iconColor,
            ),

            const SizedBox(
              height: 7,
            ),

            Text(
              title,

              textAlign:
                  TextAlign.center,

              style:
                  const TextStyle(
                color:
                    Colors.black,

                fontSize: 14,

                fontWeight:
                    FontWeight.w400,
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // BRACELET CARD
  // ============================================================

  Widget _buildBraceletCard() {
    return GestureDetector(
      onTap: () {
        _showComingSoon(
          'Bracelet Information',
        );
      },

      child: Container(
        height: 102,

        padding:
            const EdgeInsets.all(12),

        decoration:
            BoxDecoration(
          color:
              Colors.white,

          borderRadius:
              BorderRadius.circular(
            16,
          ),

          boxShadow: const [
            BoxShadow(
              color:
                  Color(0x22000000),

              blurRadius: 8,

              offset:
                  Offset(0, 3),
            ),
          ],
        ),

        child: Column(
          crossAxisAlignment:
              CrossAxisAlignment.start,

          children: [

            const Text(
              'Bracelet',

              style: TextStyle(
                color:
                    Colors.black,

                fontSize: 14,
              ),
            ),

            const Text(
              'Connected',

              style: TextStyle(
                color:
                    Colors.black,

                fontSize: 14,
              ),
            ),

            const Spacer(),

            Row(
              children: [

                Container(
                  width: 20,
                  height: 9,

                  decoration:
                      BoxDecoration(
                    color:
                        Colors.green,

                    borderRadius:
                        BorderRadius
                            .circular(
                      2,
                    ),
                  ),
                ),

                const SizedBox(
                  width: 5,
                ),

                const Text(
                  'Battery 78%',

                  style:
                      TextStyle(
                    color:
                        Colors.black,

                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // COMING SOON
  // ============================================================

  void _showComingSoon(
    String name,
  ) {
    ScaffoldMessenger.of(context)
        .showSnackBar(
      SnackBar(
        content: Text(
          '$name will be available soon.',
        ),
      ),
    );
  }
}