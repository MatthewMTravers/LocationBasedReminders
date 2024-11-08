package com.example.locationbasedreminders.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.activity.LocationActivity
import com.example.locationbasedreminders.activity.LoginActivity
import com.example.locationbasedreminders.activity.ReminderActivity
/*
*NEW IMPORTS
 */
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.locationbasedreminders.reminder.*

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.widget.EditText

class ReminderFragment : Fragment() {
    lateinit var backButton : Button
    lateinit var newButton : Button
    lateinit var findLocationButton : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()
    val db = Firebase.firestore
    val userID: Int = 123456

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        Log.d("Startup", "Intentionally empty")

        recyclerView = view.findViewById(R.id.reminderRecyclerView)
        reminderAdapter = ReminderAdapter(reminders)
        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        backButton = view.findViewById(R.id.reminderBackButton)
        newButton = view.findViewById(R.id.newReminderButton)
        findLocationButton = view.findViewById(R.id.findLocationButton)
        backButton.setOnClickListener{
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
        newButton.setOnClickListener{
            showAddReminderDialog()
        }

        findLocationButton.setOnClickListener{
            val intent = Intent(requireActivity(), LocationActivity::class.java)
            startActivity(intent)
        }

        return view
    }
//
//    private fun showAddReminderDialog() {
//        // Code to show a dialog or fragment to add a new Reminder object
//        // When adding a new reminder, update the list and notify the adapter
//        // Create an AlertDialog to confirm adding the hardcoded reminder
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//            .setTitle("Add Test Reminder")
//            .setMessage("Add a reminder with hardcoded values?")
//            .setPositiveButton("Add") { dialog, _ ->
//                // Hardcoded values for testing
//                val time = Date("Monday", 12, 25)
//                val location = Location(0.0f, 0.0f)
//                val reminder = Reminder(time, location,"Take the dog for a walk","Walk dog", userID)
//
//                // Add the hardcoded reminder to the list and notify the adapter
//                reminders.add(reminder)
//                reminderAdapter.notifyItemInserted(reminders.size - 1)
//                Toast.makeText(requireContext(), "Test reminder added!", Toast.LENGTH_SHORT).show()
//
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//        // Show the dialog
//        dialogBuilder.create().show()
//    }

    private val addReminderLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val name = data.getStringExtra("name") ?: ""
                val day = data.getStringExtra("day") ?: ""
                val hour = data.getIntExtra("hour", 0)
                val minute = data.getIntExtra("minute", 0)
                val latitude = data.getFloatExtra("latitude", 0.0f)
                val longitude = data.getFloatExtra("longitude", 0.0f)
                val description = data.getStringExtra("description") ?: ""

                // Create and add the reminder
                val time = Date(day, hour, minute)
                val location = Location(latitude, longitude)
                val reminder = Reminder(time, location, description, name, userID)
                reminders.add(reminder)
                reminderAdapter.notifyItemInserted(reminders.size - 1)
            }
        }
    }

    private fun showAddReminderDialog() {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_reminder, null)

        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add New Reminder")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                // Get user input from the dialog's EditText fields
                val name = dialogView.findViewById<EditText>(R.id.reminderNameInput).text.toString()
                val day = dialogView.findViewById<EditText>(R.id.reminderDayInput).text.toString()
                val hour = dialogView.findViewById<EditText>(R.id.reminderHourInput).text.toString().toIntOrNull() ?: 0
                val minute = dialogView.findViewById<EditText>(R.id.reminderMinuteInput).text.toString().toIntOrNull() ?: 0
                val latitude = dialogView.findViewById<EditText>(R.id.reminderLatInput).text.toString().toFloatOrNull() ?: 0.0f
                val longitude = dialogView.findViewById<EditText>(R.id.reminderLongInput).text.toString().toFloatOrNull() ?: 0.0f
                val description = dialogView.findViewById<EditText>(R.id.reminderDescriptionInput).text.toString()

                // Create new reminder and add it to the list
                val time = Date(day, hour, minute)
                val location = Location(latitude, longitude)
                val reminder = Reminder(time, location, description, name, userID)

                reminders.add(reminder)
                reminderAdapter.notifyItemInserted(reminders.size - 1)

                // Save to Firebase if needed
                saveReminderToFirebase(reminder)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun saveReminderToFirebase(reminder: Reminder) {
        val reminderMap = hashMapOf(
            "name" to reminder.name,
            "time" to mapOf("day" to reminder.time.day, "hour" to reminder.time.hour, "minute" to reminder.time.minute),
            "location" to mapOf("lat" to reminder.location.lat, "long" to reminder.location.long),
            "description" to reminder.description,
            "userID" to reminder.userID
        )

        db.collection("reminders")
            .add(reminderMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Reminder saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error adding reminder", e)
            }
    }

}
