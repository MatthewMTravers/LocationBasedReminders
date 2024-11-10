package com.example.locationbasedreminders.fragment

import android.app.AlertDialog
import android.content.DialogInterface
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationbasedreminders.reminder.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    //procedure to create and show the reminder dialogue.
    private fun showAddReminderDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_reminder, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add New Reminder")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, buttonId -> onAddReminderClick(dialogView) }
            .setNegativeButton("Cancel") { dialog, buttonId -> onCancelClick(dialog) }

        dialogBuilder.create().show()
    }
    //click handler for the add button of the reminder dialogue window
    private fun onAddReminderClick(dialogView: View) {
        val name = dialogView.findViewById<EditText>(R.id.reminderNameInput).text.toString()
        val day = dialogView.findViewById<EditText>(R.id.reminderDayInput).text.toString()
        val hour = dialogView.findViewById<EditText>(R.id.reminderHourInput).text.toString().toIntOrNull() ?: 0
        val minute = dialogView.findViewById<EditText>(R.id.reminderMinuteInput).text.toString().toIntOrNull() ?: 0
        val latitude = dialogView.findViewById<EditText>(R.id.reminderLatInput).text.toString().toFloatOrNull() ?: 0.0f
        val longitude = dialogView.findViewById<EditText>(R.id.reminderLongInput).text.toString().toFloatOrNull() ?: 0.0f
        val description = dialogView.findViewById<EditText>(R.id.reminderDescriptionInput).text.toString()

        val time = Date(day, hour, minute)
        val location = Location(latitude, longitude)
        val reminder = Reminder(time, location, description, name, userID)

        reminders.add(reminder)
        reminderAdapter.notifyItemInserted(reminders.size - 1)

        saveReminderToFirebase(reminder)
    }
    //click handle for cancel (simply closes the dialogue window)
    private fun onCancelClick(dialog: DialogInterface) {
        dialog.dismiss()
    }

    private fun saveReminderToFirebase(reminder: Reminder) {
        db.collection("reminders")
            .add(reminder)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireActivity(), "Reminder created successfully with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Error creating reminder: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
