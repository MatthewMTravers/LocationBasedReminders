package com.example.locationbasedreminders.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.activity.MapsActivity
import com.example.locationbasedreminders.activity.ReminderActivity
import com.google.android.gms.location.LocationServices

class LocationFragment : Fragment() {

    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var getLocationButton: Button
    private lateinit var backButton: Button


    // Register permission request callback
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            findLocation()  // If permission is granted, get the location
        } else {
            showToast("Location permission denied")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        // Initialize the TextViews and Button from the layout
        latitude = view.findViewById(R.id.latitude)
        longitude = view.findViewById(R.id.longitude)
        getLocationButton = view.findViewById(R.id.getLocation)
        backButton = view.findViewById(R.id.locationBack)

        backButton.setOnClickListener {
            val intent = Intent(requireActivity(), ReminderActivity::class.java)
            startActivity(intent)
        }

        // Set a click listener for the button
        getLocationButton.setOnClickListener {
            if (hasLocationPermission()) {
                findLocation()
            } else {
                requestLocationPermission()  // Request permission if not granted
            }
        }

        return view
    }

    private fun hasLocationPermission(): Boolean {
        // Check if the location permission is granted
        val result = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        // Request fine location permission
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun findLocation() {
        // Get the user's last known location
        val locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Display the latitude and longitude in the TextViews
                latitude.text = "Latitude: ${location.latitude}"
                longitude.text = "Longitude: ${location.longitude}"
            } else {
                showToast("Unable to retrieve location")
            }
        }.addOnFailureListener {
            Log.e("com.example.locationbasedreminders.fragment.LocationFragment", "Error getting location", it)
        }
    }

    private fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
