package com.example.locationbasedreminders.reminder
import java.time.*
import java.time.format.DateTimeFormatter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationbasedreminders.R

class ReminderAdapter(private val reminders: List<Reminder>) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.bind(reminder)
    }

    override fun getItemCount(): Int = reminders.size

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeAndLocationTextView: TextView = itemView.findViewById(R.id.reminderTimeLoc)
        private val nameTextView: TextView = itemView.findViewById(R.id.reminderName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.reminderDesc)

        fun bind(reminder: Reminder) {
            val nameText = "${reminder.name}"
            nameTextView.text = nameText
            
            // Format the day and time
            val timeLocText = "Time: Day ${reminder.time.day}, ${reminder.time.hour}:${reminder.time.minute}, Location: (${reminder.location.lat}, ${reminder.location.long})"
            timeAndLocationTextView.text = timeLocText

            // Set the description
            descriptionTextView.text = "Description: ${reminder.description}"
        }
    }
}



/*
* Data Definitions
*/

data class Reminder(
    var time: Date,
    var location: Location,
    var description: String,
    var name: String)

data class Date(var day: String, var hour: Int, var minute: Int)

data class Location(var lat: Float, var long: Float)