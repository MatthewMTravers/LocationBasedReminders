package com.example.locationbasedreminders.fragment

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.locationbasedreminders.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mDefaultLocation = LatLng(40.0, -83.0125)
    private var mLocation: Location? = null

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
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Called when the map is ready, sets up markers and location permission
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (hasLocationPermission()) {
            findLocation()
        } else {
            requestLocationPermission()
        }

        val osu = LatLng(40.0, -83.0125)
        mMap.addMarker(MarkerOptions()
            .position(osu)
            .title("The Ohio State University").snippet("Lat: ${osu.latitude}, Lng: ${osu.longitude}"))
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
}
