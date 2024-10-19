package com.example.locationbasedreminders.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.activity.LocationActivity
import com.example.locationbasedreminders.activity.LoginActivity
import com.example.locationbasedreminders.activity.ReminderActivity

class ReminderFragment : Fragment() {
    lateinit var backButton : Button
    lateinit var newButton : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        Log.d("Startup", "Intentionally empty")

        backButton = view.findViewById(R.id.reminderBackButton)
        newButton = view.findViewById(R.id.newReminderButton)
        backButton.setOnClickListener{
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        newButton.setOnClickListener{
            val intent = Intent(requireActivity(), LocationActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
