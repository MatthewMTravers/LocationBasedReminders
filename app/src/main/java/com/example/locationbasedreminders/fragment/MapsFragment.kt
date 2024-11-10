package com.example.locationbasedreminders.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.locationbasedreminders.GeofenceBroadcastReceiver
import com.example.locationbasedreminders.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mDefaultLocation = LatLng(40.0, -83.0125)
    private var mLocation: Location? = null
    private lateinit var geofencingClient: GeofencingClient

    // Initialize geofencing request code and intent
    private val geofenceRadius = 100f // radius in meters
    private lateinit var geofencePendingIntent: PendingIntent

    // Registers the callback for handling location permission requests
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            findLocation() // Get location if permission is granted
        } else {
            Log.d("MapsFragment", "Location permission denied")
        }
    }

    // Inflates the fragment's layout and initializes the map fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Called when the map is ready, sets up markers and location permission
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (hasLocationPermission()) {
            // Check for background location permission if fine location is already granted
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request background location permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                findLocation() // Proceed with finding location
            }
        } else {
            requestLocationPermission()
        }

        val osu = LatLng(40.0, -83.0125)
        mMap.addMarker(MarkerOptions()
            .position(osu)
            .title("The Ohio State University").snippet("Lat: ${osu.latitude}, Lng: ${osu.longitude}"))
        addGeofence(osu, "OSU_GEOFENCE")
    }

    // Checks if the app has permission to access fine location
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Requests location permission if it has not been granted
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Attempts to get the user's last known location and update the map with it
    @SuppressLint("MissingPermission")
    private fun findLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                mLocation = location
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 1f))
            } else {
                Log.d("MapsFragment", "Unable to retrieve location; using default")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 1f))
            }
        }.addOnFailureListener { exception ->
            Log.e("MapsFragment", "Error getting location", exception)
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(location: LatLng, geofenceId: String) {
        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(location.latitude, location.longitude, geofenceRadius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencePendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("MapsFragment", "Geofence added successfully")
                drawGeofenceCircle(location) // Call the method to draw the geofence circle
            }
            addOnFailureListener { e ->
                Log.e("MapsFragment", "Error adding geofence: ${e.message}")
            }
        }
    }

    // Draw a circle around the geofence
    private fun drawGeofenceCircle(location: LatLng) {
        mMap.addCircle(
            CircleOptions()
                .center(location)
                .radius(geofenceRadius.toDouble())
                .strokeWidth(3f)
                .strokeColor(0xFF0000FF.toInt())
                .fillColor(0x550000FF)
        )
    }
}
