package com.example.locationbasedreminders.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R

class AddReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val nameEditText = findViewById<EditText>(R.id.reminderNameInput)
        val dayEditText = findViewById<EditText>(R.id.reminderDayInput)
        val hourEditText = findViewById<EditText>(R.id.reminderHourInput)
        val minuteEditText = findViewById<EditText>(R.id.reminderMinuteInput)
        val latitudeEditText = findViewById<EditText>(R.id.reminderLatInput)
        val longitudeEditText = findViewById<EditText>(R.id.reminderLongInput)
        val descriptionEditText = findViewById<EditText>(R.id.reminderDescriptionInput)
        val submitButton = findViewById<Button>(R.id.submitReminderButton)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val day = dayEditText.text.toString()
            val hour = hourEditText.text.toString().toIntOrNull() ?: 0
            val minute = minuteEditText.text.toString().toIntOrNull() ?: 0
            val latitude = latitudeEditText.text.toString().toFloatOrNull() ?: 0.0f
            val longitude = longitudeEditText.text.toString().toFloatOrNull() ?: 0.0f
            val description = descriptionEditText.text.toString()

            val intent = Intent().apply {
                putExtra("name", name)
                putExtra("day", day)
                putExtra("hour", hour)
                putExtra("minute", minute)
                putExtra("latitude", latitude)
                putExtra("longitude", longitude)
                putExtra("description", description)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
