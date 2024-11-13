package com.example.locationbasedreminders.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.locationbasedreminders.reminder.Reminder

class MapsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> get() = _reminders

    init {
        fetchReminders()
    }

    // Gets all reminders from database for MapsFragment to use for markers
    private fun fetchReminders() {
        db.collection("reminders")
            .get()
            .addOnSuccessListener { result ->
                val reminderList = result.mapNotNull { document ->
                    document.toObject(Reminder::class.java)
                }
                _reminders.value = reminderList
            }
            .addOnFailureListener { _ ->
                _reminders.value = emptyList()
            }
    }
}
