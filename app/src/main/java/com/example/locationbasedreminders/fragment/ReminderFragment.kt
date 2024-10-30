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


class ReminderFragment : Fragment() {
    lateinit var backButton : Button
    lateinit var newButton : Button
    lateinit var findLocationButton : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()

    //TODO: Add new button in hotbar (FIND_LOC), and
    //lateinit var newButton : Button




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

    private fun showAddReminderDialog() {
        // Code to show a dialog or fragment to add a new Reminder object
        // When adding a new reminder, update the list and notify the adapter
        // Create an AlertDialog to confirm adding the hardcoded reminder
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add Test Reminder")
            .setMessage("Add a reminder with hardcoded values?")
            .setPositiveButton("Add") { dialog, _ ->
                // Hardcoded values for testing
                val time = Date("Monday", 12, 25)
                val location = Location(0.0f, 0.0f)
                val reminder = Reminder(time, location,"Take the dog for a walk","Walk dog")

                // Add the hardcoded reminder to the list and notify the adapter
                reminders.add(reminder)
                reminderAdapter.notifyItemInserted(reminders.size - 1)
                Toast.makeText(requireContext(), "Test reminder added!", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Show the dialog
        dialogBuilder.create().show()
    }


}
