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

class AccountFragment : Fragment(), View.OnClickListener {
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
        val view = inflater.inflate(layout.fragment_account, container, false)

        // Get references to the UI elements in the fragment layout
        usernameEditText = view.findViewById(R.id.username)
        passwordEditText = view.findViewById(R.id.password)
        passwordConfirmEditText = view.findViewById(R.id.password_confirm)

        // Create Button
        newUserButton = view.findViewById(R.id.done_button)
        newUserButton.setOnClickListener(this)
        // Clear Button
        clearButton = view.findViewById(R.id.clear_button)
        clearButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.done_button -> createAccount()
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

        // Create instance of firebase DB
        val db = Firebase.firestore

        // Validate user inputs
        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
        } else if (password != passwordConfirm) {
            Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show()
        } else {
            val user = hashMapOf(
                "username" to username,
                "password" to password
            )

            // Add the user to Fire store with an auto-generated document ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    val generatedUserId = documentReference.id  // Get the generated ID
                    Log.d(TAG, "User created with ID: $generatedUserId")
                    Toast.makeText(
                        activity,
                        "User created successfully with ID: $generatedUserId",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Optionally, store this ID or use it for further actions
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        activity,
                        "Error creating user: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
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


}


