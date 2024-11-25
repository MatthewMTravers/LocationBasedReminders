package com.example.locationbasedreminders.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationbasedreminders.reminder.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.util.UUID

class AccountViewModel(
    private val db: FirebaseFirestore = Firebase.firestore
) : ViewModel() {

    private val _operationResult = MutableLiveData<String>()
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val operationResult: LiveData<String> = _operationResult

    var currentUserId: String = ""

    fun createAccount(username: String, password: String, passwordConfirm: String) {
        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            _operationResult.value = "All fields are required!"
            return
        }
        if (password != passwordConfirm) {
            _operationResult.value = "Passwords do not match!"
            return
        }

        // Check if the username already exists
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Username is already taken
                    _operationResult.value = "Username already exists!"
                } else {
                    // Proceed with account creation if the username is unique
                    currentUserId = UUID.randomUUID().toString()

                    // Encrypt password
                    val encryptedPassword = sha256(password)

                    val user = hashMapOf(
                        "userId" to currentUserId,
                        "username" to username,
                        "password" to encryptedPassword
                    )

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            _operationResult.value = "User created successfully with ID: $currentUserId"
                        }
                        .addOnFailureListener { e ->
                            _operationResult.value = "Error creating user: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                _operationResult.value = "Error checking username: ${e.message}"
            }
    }

    // Returns user based on username
    fun getUserId(username: String, onComplete: (String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val userId = document.getString("userId")
                    onComplete(userId)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    // Returns reminder from geofenceId for user in notification
    fun getReminderByGeofenceId(geofenceId: String, callback: (Reminder?) -> Unit) {
        db.collection("reminders")
            .whereEqualTo("geofenceId", geofenceId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null)
                } else {
                    // Assuming only one reminder per geofenceId, get the first document
                    val reminder = documents.documents[0].toObject(Reminder::class.java)
                    callback(reminder)
                }
            }
            .addOnFailureListener { _ ->
                callback(null)
            }
    }


    fun updateSingleUser(oldUserName: String, newUsername: String, oldPassword: String, newPassword: String) {
        getUserId(oldUserName) { userId ->
            if (userId != null) {
                // Proceed with updating using the fetched userId
                db.collection("users")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val updates = mutableMapOf<String, Any>()
                            val encryptedPassword = sha256(newPassword)
                            updates["username"] = newUsername
                            updates["password"] = encryptedPassword

                            db.collection("users").document(document.id)
                                .update(updates)
                                .addOnSuccessListener {
                                    _operationResult.value = "User updated successfully!"
                                    _navigateToLogin.value = true
                                }
                                .addOnFailureListener { e ->
                                    _operationResult.value = "Error updating user: ${e.message}"
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        _operationResult.value = "Error fetching user for update: ${e.message}"
                    }
            } else {
                _operationResult.value = "Error: User not found. Please check the username."
            }
        }
    }

    fun deleteCurrentUser() {
        if (currentUserId.isEmpty()) {
            _operationResult.value = "Error: User ID not found."
            return
        }
        db.collection("users")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(document.id).delete()
                        .addOnSuccessListener {
                            _operationResult.value = "User deleted successfully!"
                        }
                        .addOnFailureListener { e ->
                            _operationResult.value = "Error deleting user: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                _operationResult.value = "Error fetching user for deletion: ${e.message}"
            }
    }

    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
