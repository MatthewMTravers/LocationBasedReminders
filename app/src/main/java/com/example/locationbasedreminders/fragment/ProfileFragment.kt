package com.example.locationbasedreminders.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.activity.MapsActivity
import com.example.locationbasedreminders.model.AccountViewModel

class ProfileFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        usernameEditText = view.findViewById(R.id.editTextUsername)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        updateButton = view.findViewById(R.id.updateProfile)
        deleteButton = view.findViewById(R.id.deleteAccount)
        backButton = view.findViewById(R.id.profileBack)


        // Set a click listener for the update button
        updateButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString()
            val newPassword = passwordEditText.text.toString()

            // Basic validation
            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.updateSingleUser(newUsername, newPassword)
            }
        }

        deleteButton.setOnClickListener { viewModel.deleteCurrentUser() }

        backButton.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
