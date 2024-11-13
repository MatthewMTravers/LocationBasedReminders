package com.example.locationbasedreminders.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationbasedreminders.reminder.Reminder

class SharedViewModel : ViewModel() {
    private val _selectedReminder = MutableLiveData<Reminder>()
    val selectedReminder: LiveData<Reminder> get() = _selectedReminder

    fun selectReminder(reminder: Reminder) {
        _selectedReminder.value = reminder
    }
}