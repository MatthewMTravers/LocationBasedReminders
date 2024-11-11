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

//implement the reminder deletion interface implemented in reminder.kt, so that it is visible to both classes
class ReminderFragment : Fragment(), ReminderDeletion {
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
        reminderAdapter = ReminderAdapter(reminders, this)
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
        loadRemindersFromFirebase()

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
                reminder.reminderFirebaseID = documentReference.id
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Error creating reminder: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRemindersFromFirebase() {
        db.collection("reminders")
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { documents ->
                reminders.clear()
                for (document in documents) {
                    val reminder = document.toObject(Reminder::class.java)
                    reminder.reminderFirebaseID = document.id
                    reminders.add(reminder)

                }
                //notify that data has changed, and update recycler view.
                reminderAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Error fetching reminders: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun deleteReminder(reminder: Reminder, pos: Int) {
        //Remove reminder from reminders list; update recyclerview
        reminders.removeAt(pos)
        reminderAdapter.notifyDataSetChanged()

        //delete reminder from firebase
        if (reminder.reminderFirebaseID != "") {
            val id = reminder.reminderFirebaseID
            db.collection("reminders").document(id).delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reminder deleted from Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error deleting reminder: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
