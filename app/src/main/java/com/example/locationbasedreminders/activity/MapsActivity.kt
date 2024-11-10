package com.example.locationbasedreminders.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.MapsFragment

class MapsActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var reminderButton: Button
    private lateinit var profileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize buttons and set click listeners
        backButton = findViewById(R.id.mapsBack)
        reminderButton = findViewById(R.id.mapsReminder)
        profileButton = findViewById(R.id.mapsProfile)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        reminderButton.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_container, MapsFragment())
                .commit()
        }
    }
}
