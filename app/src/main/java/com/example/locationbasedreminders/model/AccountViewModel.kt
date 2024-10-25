package com.example.locationbasedreminders.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AccountViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _operationResult = MutableLiveData<String>()
    val operationResult: LiveData<String> = _operationResult

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

    fun getSingleUser() {
        db.collection("users")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
                _operationResult.value = "User fetched successfully"
            }
            .addOnFailureListener { e ->
                _operationResult.value = "Error fetching user: ${e.message}"
            }
    }

    fun updateSingleUser(username: String, password: String) {
        db.collection("users")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(document.id)
                        .update("username", username, "password", password)
                        .addOnSuccessListener {
                            _operationResult.value = "User updated successfully!"
                        }
                        .addOnFailureListener { e ->
                            _operationResult.value = "Error updating user: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                _operationResult.value = "Error fetching user for update: ${e.message}"
            }
    }

    fun deleteAllDocumentsFromCollection() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("TAG", "Document with ID ${document.id} deleted")
                        }
                        .addOnFailureListener { e ->
                            Log.d("TAG", "Error deleting document: $e")
                        }
                }
                _operationResult.value = "All documents deleted"
            }
            .addOnFailureListener { e ->
                _operationResult.value = "Error fetching documents: ${e.message}"
            }
    }
}
