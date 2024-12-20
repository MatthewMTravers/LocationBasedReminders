package com.example.locationbasedreminders.fragment

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
import com.example.locationbasedreminders.activity.LoginActivity
import com.example.locationbasedreminders.model.LoginViewModel

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Get references to the UI elements in the fragment layout
        val usernameEditText: EditText = view.findViewById(R.id.username_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_text)
        val loginButton: Button = view.findViewById(R.id.login_button)

        // Observe login result from ViewModel
        loginViewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                (activity as LoginActivity).onLoginSuccess()
            } else {
                Toast.makeText(activity, "Invalid credentials!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the login button click event
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginViewModel.checkCredentials(username, password)
            Toast.makeText(activity, "One moment while we check your credentials!", Toast.LENGTH_LONG).show()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button to switch to register fragment
        val registerButton: Button = view.findViewById(R.id.new_user_button)
        registerButton.setOnClickListener {
            (activity as LoginActivity).switchToAccountFragment()
        }
    }
}
