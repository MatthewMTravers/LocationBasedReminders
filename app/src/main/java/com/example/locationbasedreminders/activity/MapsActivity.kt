// MapsActivity.kt
package com.example.locationbasedreminders.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.MapsFragment

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Load MapsFragment dynamically if not already loaded
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_container, MapsFragment())
                .commit()
        }
    }
}
