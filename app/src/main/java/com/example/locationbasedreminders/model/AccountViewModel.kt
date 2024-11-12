package com.example.locationbasedreminders.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AccountViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _operationResult = MutableLiveData<String>()
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val operationResult: LiveData<String> = _operationResult
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    private var currentUserId: String = ""

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
                    val user = hashMapOf(
                        "userId" to currentUserId,
                        "username" to username,
                        "password" to password
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


    //    fun getSingleUser() {
//        db.collection("users")
//            .whereEqualTo("userId", currentUserId)
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d("TAG", "${document.id} => ${document.data}")
//                }
//                _operationResult.value = "User fetched successfully"
//            }
//            .addOnFailureListener { e ->
//                _operationResult.value = "Error fetching user: ${e.message}"
//            }
//    }
    fun getUserId(username: String, onComplete: (String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val userId = document.getString("userId")
                    onComplete(userId)  // Pass the userId back to the caller
                } else {
                    onComplete(null)  // No matching username found
                }
            }
            .addOnFailureListener {
                onComplete(null)  // Error occurred while fetching user
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
                            updates["username"] = newUsername
                            updates["password"] = newPassword

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

    fun resetNavigation() {
        _navigateToLogin.value = false
    }
}
