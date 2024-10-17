package com.example.locationbasedreminders.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.R.*
import com.example.locationbasedreminders.activity.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AccountFragment : Fragment(), View.OnClickListener {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var newUserButton: Button
    private lateinit var clearButton: Button
    private lateinit var exitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var currentUseId: String
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout.fragment_account, container, false)

        // Get references to the UI elements in the fragment layout
        usernameEditText = view.findViewById(R.id.username)
        passwordEditText = view.findViewById(R.id.password)
        passwordConfirmEditText = view.findViewById(R.id.password_confirm)

        // Create Button
        newUserButton = view.findViewById(R.id.create_button)
        newUserButton.setOnClickListener(this)

        // Clear Button
        clearButton = view.findViewById(R.id.clear_button)
        clearButton.setOnClickListener(this)

        // TEMPORARY: Delete Button
        deleteButton = view.findViewById(R.id.delete_button)
        deleteButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.create_button -> createAccount()
            R.id.delete_button -> deleteAllDocumentsFromCollection()
            R.id.clear_button -> {
                usernameEditText.text.clear()
                passwordEditText.text.clear()
                passwordConfirmEditText.text.clear()
            }
        }
    }

    private fun createAccount() {
        val activity = requireActivity()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val passwordConfirm = passwordConfirmEditText.text.toString()

        // Validate user inputs
        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
        } else if (password != passwordConfirm) {
            Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show()
        } else {
            // Generate a unique user ID using UUID
            this.currentUseId = UUID.randomUUID().toString()

            val user = hashMapOf(
                "userId" to this.currentUseId ,
                "username" to username,
                "password" to password
            )

            // TEMPORARY: adds user to the collection
            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "User created with ID: $this.currentUseId ")
                    Toast.makeText(
                        activity,
                        "User created successfully with ID: $this.currentUseId ",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        activity,
                        "Error creating user: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            // TEMPORARY: performs a 'READ' and logs all users
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button to switch to register fragment
        exitButton = view.findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            (activity as LoginActivity).exitNewAccount()
        }
    }

    // TEMPORARY
    private fun deleteAllDocumentsFromCollection() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(document.id).delete()
                        .addOnSuccessListener {
                            println("Document with ID ${document.id} deleted")
                        }
                        .addOnFailureListener { e ->
                            println("Error deleting document: $e")
                        }
                }
            }.addOnFailureListener { e ->
                println("Error getting documents: $e")
            }
    }
}


