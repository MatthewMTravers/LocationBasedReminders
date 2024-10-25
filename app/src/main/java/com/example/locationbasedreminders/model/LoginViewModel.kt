package com.example.locationbasedreminders.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // LiveData for the login result
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    // Function to check credentials
    fun checkCredentials(username: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                _loginResult.value = !documents.isEmpty
            }
            .addOnFailureListener {
                _loginResult.value = false
            }
    }
}
