package com.example.locationbasedreminders.activity

import AccountFragment
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.LoginFragment

/* Main container for handling the login screen, handles the login and
registration fragments of the log in functionality */

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Load the LoginFragment when the activity starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commitNow()
        }
    }

    // Function to switch fragments (login to register, etc.)
    fun switchToAccountFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AccountFragment())
            .addToBackStack(null)
            .commit()
    }

    fun exitNewAccount(){
        setContentView(R.layout.activity_login)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commitNow()
        Log.d("ExitLogin", "User clicked exit account creation, return to login")
    }
}
