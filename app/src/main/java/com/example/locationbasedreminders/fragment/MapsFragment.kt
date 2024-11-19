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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.locationbasedreminders.GeofenceBroadcastReceiver
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.model.MapsViewModel
import com.example.locationbasedreminders.reminder.Reminder
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
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var geofencePendingIntent: PendingIntent
    private val addedReminders = mutableSetOf<String>()

    // Registers the callback for handling location permission requests
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            findLocation()
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
        mapsViewModel = ViewModelProvider(requireActivity())[MapsViewModel::class.java]

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Called when the map is ready, sets up markers and location permission
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (hasLocationPermission()) {
            // UI enhancements
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isCompassEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true
            mMap.uiSettings.isTiltGesturesEnabled = true

            // Check for background location permission if fine location is already granted
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request background location permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                findLocation()
            }
        } else {
            requestLocationPermission()
        }

        // Standard marker for school
        val osu = LatLng(40.0, -83.0125)
        mMap.addMarker(MarkerOptions()
            .position(osu)
            .title("The Ohio State University")
            .snippet("Lat: ${osu.latitude}, Lng: ${osu.longitude}")
        )

        // Add reminders to map and track status
        mapsViewModel.reminders.observe(viewLifecycleOwner) { reminders ->
            reminders.forEach { reminder ->
                if (!addedReminders.contains(reminder.reminderFirebaseID)) {
                    addMarkerAndGeofence(reminder)
                    addedReminders.add(reminder.reminderFirebaseID)
                }
            }
        }
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
    fun addMarkerAndGeofence(reminder: Reminder) {
        val reminderPosition = LatLng(reminder.location.lat.toDouble(), reminder.location.long.toDouble())

        //Add marker on map
        mMap.addMarker(MarkerOptions()
            .position(reminderPosition)
            .title(reminder.name)
            .snippet("Lat: ${reminder.location.lat}, Lng: ${reminder.location.long}")
        )

        // TODO: TEMPORARY
        if (mLocation != null) {
            val userLocation = Location("user")
            userLocation.latitude = mLocation!!.latitude
            userLocation.longitude = mLocation!!.longitude
            val geofenceCenter = Location("geofence")
            geofenceCenter.latitude = reminder.location.lat.toDouble()
            geofenceCenter.longitude = reminder.location.long.toDouble()
            val distance = userLocation.distanceTo(geofenceCenter)
            if (distance <= reminder.geofenceRadius) {
                Toast.makeText(requireContext(), "User is inside the geofence zone! ${reminder.description}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "User is outside the geofence zone. Distance: $distance meters.", Toast.LENGTH_LONG).show()
            }
        }

        //Create geofence and prepare to add
        val geofence = Geofence.Builder()
            .setRequestId(reminder.geofenceID)
            .setCircularRegion(reminder.location.lat.toDouble(), reminder.location.long.toDouble(),
                reminder.geofenceRadius
            )
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

        //Add geofence and visual circle
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("MapsFragment", "Geofence added successfully")

                mMap.addCircle(
                    CircleOptions()
                        .center(reminderPosition)
                        .radius(reminder.geofenceRadius.toDouble())
                        .strokeWidth(3f)
                        .strokeColor(0xFF0000FF.toInt())
                        .fillColor(0x550000FF)
                )
            }
            addOnFailureListener { e ->
                Log.e("MapsFragment", "Error adding geofence: ${e.message}")
            }
        }
    }
}
