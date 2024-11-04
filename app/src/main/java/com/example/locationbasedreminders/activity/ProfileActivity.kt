package com.example.locationbasedreminders.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.ProfileFragment

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Load ProfileFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment_container, ProfileFragment())
                .commit()
        }
    }
}
