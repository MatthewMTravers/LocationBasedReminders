package com.example.locationbasedreminders.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.ReminderFragment

/* Main container for handling the login screen, handles the login and
registration fragments of the log in functionality */
class ReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.reminder_fragment_container, ReminderFragment())
                .commitNow()
        }
    }
}