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
import com.example.locationbasedreminders.model.AccountViewModel
import androidx.navigation.fragment.findNavController

class AccountFragment : Fragment(), View.OnClickListener {
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var newUserButton: Button
    private lateinit var clearButton: Button
    private lateinit var exitButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        usernameEditText = view.findViewById(R.id.username)
        passwordEditText = view.findViewById(R.id.password)
        passwordConfirmEditText = view.findViewById(R.id.password_confirm)

        clearButton = view.findViewById(R.id.clear_button)
        clearButton.setOnClickListener(this)

        newUserButton = view.findViewById(R.id.create_button)
        newUserButton.setOnClickListener(this)

        exitButton = view.findViewById(R.id.exit_button)
        exitButton.setOnClickListener { (activity as LoginActivity).exitNewAccount() }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
        }

//        viewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
//            if (navigate == true) {
//                findNavController().navigate(R.id.login_activity)
//                viewModel.resetNavigation()
//            }
//        }
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.create_button -> viewModel.createAccount(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                passwordConfirmEditText.text.toString()
            )
            R.id.clear_button -> {
                usernameEditText.text.clear()
                passwordEditText.text.clear()
                passwordConfirmEditText.text.clear()
            }
        }
    }
}
