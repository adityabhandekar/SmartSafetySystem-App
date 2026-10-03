package women_safety_api.contact;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@Service
public class TrustedContactService {

    private final Firestore firestore;

    public TrustedContactService(Firestore firestore) {
        this.firestore = firestore;
    }

    // ============================================================
    // ADD TRUSTED CONTACT
    // ============================================================

    public TrustedContact addContact(
            String userId,
            TrustedContact contact) {

        try {

            String contactId =
                    UUID.randomUUID().toString();

            // Set system-controlled fields
            contact.setId(contactId);
            contact.setUserId(userId);

            // Save complete contact object
            DocumentReference document =
                    firestore
                            .collection("trusted_contacts")
                            .document(contactId);

            document.set(contact).get();

            return contact;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to add trusted contact",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL TRUSTED CONTACTS FOR USER
    // ============================================================

    public List<TrustedContact> getContacts(
            String userId) {

        try {

            ApiFuture<QuerySnapshot> future =
                    firestore
                            .collection("trusted_contacts")
                            .whereEqualTo(
                                    "userId",
                                    userId
                            )
                            .get();

            List<QueryDocumentSnapshot> documents =
                    future.get().getDocuments();

            List<TrustedContact> contacts =
                    new ArrayList<>();

            for (QueryDocumentSnapshot document :
                    documents) {

                TrustedContact contact =
                        document.toObject(
                                TrustedContact.class
                        );

                // Firestore document ID
                // must be copied manually
                contact.setId(
                        document.getId()
                );

                contacts.add(contact);
            }

            return contacts;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load trusted contacts",
                    e
            );
        }
    }

    // ============================================================
    // DELETE TRUSTED CONTACT
    // ============================================================

    public void deleteContact(
            String userId,
            String contactId) {

        try {

            DocumentSnapshot snapshot =
                    firestore
                            .collection("trusted_contacts")
                            .document(contactId)
                            .get()
                            .get();

            // ----------------------------------------------------
            // CONTACT DOES NOT EXIST
            // ----------------------------------------------------

            if (!snapshot.exists()) {

                throw new RuntimeException(
                        "Trusted contact not found"
                );
            }

            // ----------------------------------------------------
            // GET OWNER USER ID
            // ----------------------------------------------------

            String contactOwnerUserId =
                    snapshot.getString("userId");

            // ----------------------------------------------------
            // SECURITY CHECK
            // ----------------------------------------------------

            if (contactOwnerUserId == null ||
                    !userId.equals(contactOwnerUserId)) {

                throw new RuntimeException(
                        "You are not allowed to delete this contact"
                );
            }

            // ----------------------------------------------------
            // DELETE FIRESTORE DOCUMENT
            // ----------------------------------------------------

            firestore
                    .collection("trusted_contacts")
                    .document(contactId)
                    .delete()
                    .get();

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete trusted contact",
                    e
            );
        }
    }
}