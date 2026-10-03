import 'package:flutter/material.dart';
import '../services/api_service.dart';

class TrustedContactsScreen extends StatefulWidget {
  const TrustedContactsScreen({super.key});

  @override
  State<TrustedContactsScreen> createState() =>
      _TrustedContactsScreenState();
}

class _TrustedContactsScreenState
    extends State<TrustedContactsScreen> {

  List<Map<String, dynamic>> contacts = [];

  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    loadContacts();
  }

  // ============================================================
  // LOAD TRUSTED CONTACTS
  // ============================================================

  Future<void> loadContacts() async {

    try {

      setState(() {
        isLoading = true;
      });

      final data =
          await ApiService.getTrustedContacts();

      if (!mounted) return;

      setState(() {
        contacts =
            List<Map<String, dynamic>>.from(data);
        isLoading = false;
      });

    } catch (e) {

      if (!mounted) return;

      setState(() {
        isLoading = false;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content:
              Text('Failed to load contacts: $e'),
        ),
      );
    }
  }

  // ============================================================
  // ADD TRUSTED CONTACT
  // ============================================================

  void showAddContactDialog() {

    final nameController =
        TextEditingController();

    final phoneController =
        TextEditingController();

    final emailController =
        TextEditingController();

    showDialog(
      context: context,
      builder: (dialogContext) {

        bool isAdding = false;

        return StatefulBuilder(
          builder: (context, setDialogState) {

            return AlertDialog(

              title: const Text(
                'Add Trusted Contact',
              ),

              content: SingleChildScrollView(

                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [

                    // ------------------------------------------------
                    // NAME
                    // ------------------------------------------------

                    TextField(
                      controller:
                          nameController,

                      textCapitalization:
                          TextCapitalization.words,

                      decoration:
                          const InputDecoration(
                        labelText:
                            'Contact Name',
                        hintText:
                            'Enter contact name',
                        prefixIcon:
                            Icon(
                          Icons.person_outline,
                        ),
                        border:
                            OutlineInputBorder(),
                      ),
                    ),

                    const SizedBox(height: 15),

                    // ------------------------------------------------
                    // PHONE
                    // ------------------------------------------------

                    TextField(
                      controller:
                          phoneController,

                      keyboardType:
                          TextInputType.phone,

                      decoration:
                          const InputDecoration(
                        labelText:
                            'Phone Number',
                        hintText:
                            '+919XXXXXXXXX',
                        prefixIcon:
                            Icon(
                          Icons.phone_outlined,
                        ),
                        border:
                            OutlineInputBorder(),
                      ),
                    ),

                    const SizedBox(height: 15),

                    // ------------------------------------------------
                    // APP ACCOUNT EMAIL
                    // ------------------------------------------------

                    TextField(
                      controller:
                          emailController,

                      keyboardType:
                          TextInputType.emailAddress,

                      decoration:
                          const InputDecoration(
                        labelText:
                            'App Account Email (Optional)',
                        hintText:
                            'example@email.com',
                        helperText:
                            'Enter this only if the contact uses the app.',
                        prefixIcon:
                            Icon(
                          Icons.email_outlined,
                        ),
                        border:
                            OutlineInputBorder(),
                      ),
                    ),
                  ],
                ),
              ),

              actions: [

                // ------------------------------------------------
                // CANCEL
                // ------------------------------------------------

                TextButton(
                  onPressed:
                      isAdding
                          ? null
                          : () {
                              Navigator.pop(
                                dialogContext,
                              );
                            },

                  child:
                      const Text('Cancel'),
                ),

                // ------------------------------------------------
                // ADD
                // ------------------------------------------------

                ElevatedButton(

                  onPressed:
                      isAdding
                          ? null
                          : () async {

                              final name =
                                  nameController
                                      .text
                                      .trim();

                              final phone =
                                  phoneController
                                      .text
                                      .trim();

                              final email =
                                  emailController
                                      .text
                                      .trim();

                              // ------------------------------------
                              // VALIDATION
                              // ------------------------------------

                              if (name.isEmpty ||
                                  phone.isEmpty) {

                                ScaffoldMessenger
                                    .of(context)
                                    .showSnackBar(
                                  const SnackBar(
                                    content: Text(
                                      'Please enter name and phone number',
                                    ),
                                  ),
                                );

                                return;
                              }

                              setDialogState(() {
                                isAdding = true;
                              });

                              try {

                                String?
                                    contactUserId;

                                // ----------------------------------
                                // APP CONTACT
                                // ----------------------------------

                                if (email.isNotEmpty) {

                                  try {

                                    final user =
                                        await ApiService
                                            .lookupUserByEmail(
                                      email: email,
                                    );

                                    contactUserId =
                                        user['userId']
                                            ?.toString();

                                    if (contactUserId ==
                                            null ||
                                        contactUserId!
                                            .isEmpty) {

                                      throw Exception(
                                        'User ID was not returned',
                                      );
                                    }

                                  } catch (e) {

                                    if (!context.mounted) {
                                      return;
                                    }

                                    setDialogState(() {
                                      isAdding = false;
                                    });

                                    ScaffoldMessenger
                                        .of(context)
                                        .showSnackBar(
                                      SnackBar(
                                        content: Text(
                                          'App account not found: $e',
                                        ),
                                      ),
                                    );

                                    return;
                                  }
                                }

                                // ----------------------------------
                                // SAVE CONTACT
                                // ----------------------------------

                                await ApiService
                                    .addTrustedContact(
                                  name: name,
                                  phone: phone,
                                  contactUserId:
                                      contactUserId,
                                );

                                if (!context.mounted) {
                                  return;
                                }

                                Navigator.pop(
                                  dialogContext,
                                );

                                await loadContacts();

                                if (!mounted) return;

                                ScaffoldMessenger
                                    .of(context)
                                    .showSnackBar(
                                  SnackBar(
                                    content: Text(
                                      contactUserId != null
                                          ? 'App contact added successfully'
                                          : 'SMS contact added successfully',
                                    ),
                                  ),
                                );

                              } catch (e) {

                                if (!context.mounted) {
                                  return;
                                }

                                setDialogState(() {
                                  isAdding = false;
                                });

                                ScaffoldMessenger
                                    .of(context)
                                    .showSnackBar(
                                  SnackBar(
                                    content: Text(
                                      'Failed to add contact: $e',
                                    ),
                                  ),
                                );
                              }
                            },

                  child:
                      isAdding
                          ? const SizedBox(
                              height: 20,
                              width: 20,
                              child:
                                  CircularProgressIndicator(
                                strokeWidth: 2,
                              ),
                            )
                          : const Text('Add'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  // ============================================================
  // DELETE CONTACT
  // ============================================================

  Future<void> deleteContact(
      int index) async {

    final contactId =
        contacts[index]['id']?.toString();

    if (contactId == null ||
        contactId.isEmpty) {

      ScaffoldMessenger.of(context)
          .showSnackBar(
        const SnackBar(
          content:
              Text('Contact ID not found'),
        ),
      );

      return;
    }

    try {

      await ApiService.deleteTrustedContact(
        contactId: contactId,
      );

      await loadContacts();

      if (!mounted) return;

      ScaffoldMessenger.of(context)
          .showSnackBar(
        const SnackBar(
          content:
              Text(
            'Trusted contact deleted successfully',
          ),
        ),
      );

    } catch (e) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content:
              Text(
            'Failed to delete contact: $e',
          ),
        ),
      );
    }
  }

  // ============================================================
  // BUILD
  // ============================================================

  @override
  Widget build(BuildContext context) {

    return Scaffold(

      // ==========================================================
      // APP BAR
      // ==========================================================

      appBar: AppBar(

        title:
            const Text(
          'Trusted Contacts',
        ),

        centerTitle: true,

        actions: [

          IconButton(
            icon:
                const Icon(Icons.refresh),

            onPressed:
                isLoading
                    ? null
                    : loadContacts,

            tooltip:
                'Refresh',
          ),
        ],
      ),

      // ==========================================================
      // BODY
      // ==========================================================

      body:

          isLoading

              // --------------------------------------------------
              // LOADING
              // --------------------------------------------------

              ? const Center(
                  child:
                      CircularProgressIndicator(),
                )

              // --------------------------------------------------
              // EMPTY
              // --------------------------------------------------

              : contacts.isEmpty

                  ? Center(
                      child: Padding(
                        padding:
                            const EdgeInsets.all(30),

                        child: Column(
                          mainAxisAlignment:
                              MainAxisAlignment.center,

                          children: [

                            Icon(
                              Icons.people_outline,
                              size: 80,
                              color:
                                  Colors.pink.shade200,
                            ),

                            const SizedBox(
                              height: 20,
                            ),

                            const Text(
                              'No trusted contacts yet',

                              textAlign:
                                  TextAlign.center,

                              style:
                                  TextStyle(
                                fontSize: 20,
                                fontWeight:
                                    FontWeight.bold,
                              ),
                            ),

                            const SizedBox(
                              height: 10,
                            ),

                            const Text(
                              'Add people who should receive your emergency alerts.',

                              textAlign:
                                  TextAlign.center,

                              style:
                                  TextStyle(
                                color:
                                    Colors.grey,
                                fontSize: 15,
                              ),
                            ),

                            const SizedBox(
                              height: 25,
                            ),

                            ElevatedButton.icon(

                              onPressed:
                                  showAddContactDialog,

                              icon:
                                  const Icon(
                                Icons.add,
                              ),

                              label:
                                  const Text(
                                'Add Trusted Contact',
                              ),
                            ),
                          ],
                        ),
                      ),
                    )

                  // --------------------------------------------------
                  // CONTACT LIST
                  // --------------------------------------------------

                  : RefreshIndicator(

                      onRefresh:
                          loadContacts,

                      child:
                          ListView.builder(

                        padding:
                            const EdgeInsets.all(16),

                        itemCount:
                            contacts.length,

                        itemBuilder:
                            (context, index) {

                          final contact =
                              contacts[index];

                          final String name =
                              contact['name']
                                      ?.toString() ??
                                  'Unknown';

                          final String phone =
                              contact['phone']
                                      ?.toString() ??
                                  'No phone number';

                          final String?
                              contactUserId =
                              contact[
                                      'contactUserId']
                                  ?.toString();

                          final bool
                              isAppContact =
                              contactUserId != null &&
                                  contactUserId
                                      .isNotEmpty;

                          final String initial =
                              name.isNotEmpty
                                  ? name[0]
                                      .toUpperCase()
                                  : '?';

                          return Card(

                            margin:
                                const EdgeInsets.only(
                              bottom: 12,
                            ),

                            elevation: 2,

                            child:
                                ListTile(

                              contentPadding:
                                  const EdgeInsets
                                      .symmetric(
                                horizontal: 16,
                                vertical: 8,
                              ),

                              // ------------------------------------
                              // PROFILE
                              // ------------------------------------

                              leading:
                                  CircleAvatar(
                                radius: 25,

                                child:
                                    Text(
                                  initial,

                                  style:
                                      const TextStyle(
                                    fontSize: 18,
                                    fontWeight:
                                        FontWeight.bold,
                                  ),
                                ),
                              ),

                              // ------------------------------------
                              // NAME
                              // ------------------------------------

                              title:
                                  Text(
                                name,

                                style:
                                    const TextStyle(
                                  fontWeight:
                                      FontWeight.bold,
                                  fontSize: 16,
                                ),
                              ),

                              // ------------------------------------
                              // PHONE + TYPE
                              // ------------------------------------

                              subtitle:
                                  Padding(
                                padding:
                                    const EdgeInsets
                                        .only(
                                  top: 5,
                                ),

                                child:
                                    Column(
                                  crossAxisAlignment:
                                      CrossAxisAlignment
                                          .start,

                                  children: [

                                    Text(
                                      phone,

                                      style:
                                          const TextStyle(
                                        color:
                                            Colors.grey,
                                      ),
                                    ),

                                    const SizedBox(
                                      height: 4,
                                    ),

                                    Row(
                                      children: [

                                        Icon(
                                          isAppContact
                                              ? Icons
                                                  .notifications_active
                                              : Icons
                                                  .sms_outlined,

                                          size: 16,

                                          color:
                                              isAppContact
                                                  ? Colors
                                                      .green
                                                  : Colors
                                                      .orange,
                                        ),

                                        const SizedBox(
                                          width: 5,
                                        ),

                                        Text(
                                          isAppContact
                                              ? 'App + FCM'
                                              : 'SMS8',

                                          style:
                                              TextStyle(
                                            fontSize:
                                                12,
                                            fontWeight:
                                                FontWeight
                                                    .w600,
                                            color:
                                                isAppContact
                                                    ? Colors
                                                        .green
                                                    : Colors
                                                        .orange,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),

                              // ------------------------------------
                              // DELETE
                              // ------------------------------------

                              trailing:
                                  IconButton(
                                icon:
                                    const Icon(
                                  Icons
                                      .delete_outline,
                                  color:
                                      Colors.red,
                                ),

                                onPressed:
                                    () => deleteContact(
                                  index,
                                ),
                              ),
                            ),
                          );
                        },
                      ),
                    ),

      // ==========================================================
      // FLOATING ACTION BUTTON
      // ==========================================================

      floatingActionButton:
          contacts.isNotEmpty

              ? FloatingActionButton(
                  onPressed:
                      showAddContactDialog,

                  child:
                      const Icon(
                    Icons.add,
                  ),
                )

              : null,
    );
  }
}