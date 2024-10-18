package com.example.locationbasedreminders.activity

import com.example.locationbasedreminders.fragment.LocationFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Load the LoginFragment when the activity starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LocationFragment())
                .commitNow()
        }
    }
}