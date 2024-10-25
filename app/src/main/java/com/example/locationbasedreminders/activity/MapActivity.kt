package com.example.locationbasedreminders.activity

import com.example.locationbasedreminders.fragment.MapFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    // Code from Chap. 33, Big Nerd Ranch Guide to Android Programming, 3rd ed.
    override fun onResume() {
        super.onResume()
        val apiAvailability = GoogleApiAvailability.getInstance()
        val errorCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (errorCode != ConnectionResult.SUCCESS) {
            val errorDialog = apiAvailability.getErrorDialog(
                this, errorCode, REQUEST_ERROR
            ) {
                finish()
            }!!
            errorDialog.show()
        }
    }

    companion object {
        private const val REQUEST_ERROR = 0
    }

}