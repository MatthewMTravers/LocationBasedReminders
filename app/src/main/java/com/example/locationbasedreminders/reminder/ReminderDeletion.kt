package com.example.locationbasedreminders.reminder

interface ReminderDeletion{
    fun deleteReminder(reminder: Reminder, pos: Int)
}