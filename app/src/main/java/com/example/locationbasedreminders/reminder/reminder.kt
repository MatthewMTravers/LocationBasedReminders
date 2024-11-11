package com.example.locationbasedreminders.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.reminder.ReminderDeletion

/*
*Reminder adapter for the recycler view
*/

class ReminderAdapter(private val reminders: List<Reminder>, private val reminderDeletion: ReminderDeletion) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder, parent, false)
        return ReminderViewHolder(view, reminderDeletion)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.populateReminder(reminder)
    }

    override fun getItemCount(): Int{
        return reminders.size
    }

    //This is essentially its own mini "fragment" class representing a single UI element that is contained in the recyclerview.
    //Reference UI elements by resource lookup, and then after binding the viewholder to the specific
    //elemement in the recyclerview Data structure, the absolute path lookup of the elements is automatically
    //handled.
    class ReminderViewHolder(itemView: View, private val reminderDeletion: ReminderDeletion) : RecyclerView.ViewHolder(itemView) {
        private val timeAndLocationTextView: TextView = itemView.findViewById(R.id.reminderTimeLoc)
        private val nameTextView: TextView = itemView.findViewById(R.id.reminderName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.reminderDesc)
        private val reminderDeleteButton: Button = itemView.findViewById(R.id.deleteReminderButton)

        fun populateReminder(reminder: Reminder) {
            val nameText = reminder.name
            nameTextView.text = nameText

            val timeLocText = "Time: Day ${reminder.time.day}, ${reminder.time.hour}:${reminder.time.minute}, Location: (${reminder.location.lat}, ${reminder.location.long})"
            timeAndLocationTextView.text = timeLocText

            descriptionTextView.text = "Description:" + reminder.description

            reminderDeleteButton.setOnClickListener {
                reminderDeletion.deleteReminder(reminder, bindingAdapterPosition)
            }
        }

    }
}

/*
* Data Definitions
*/

data class Reminder(
    var time: Date = Date("MISSING", 0, 0), //string not null because cant display null string
    var location: Location = Location(0.0f, 0.0f),
    var description: String = "",
    var name: String = "",
    var userID: Int = 0,
    var reminderFirebaseID: String = ""
)

data class Date(
    var day: String = "MISSING", //Not null because cant display null string
    var hour: Int = 0,
    var minute: Int = 0
)

data class Location(
    var lat: Float = 0.0f,
    var long: Float = 0.0f
)