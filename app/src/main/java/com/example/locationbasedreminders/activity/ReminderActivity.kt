package com.example.locationbasedreminders.activity

import com.example.locationbasedreminders.fragment.AccountFragment
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.locationbasedreminders.R
import com.example.locationbasedreminders.fragment.LocationFragment
import com.example.locationbasedreminders.fragment.LoginFragment
import com.example.locationbasedreminders.fragment.ReminderFragment
//import com.example.locationbasedreminders.database.Database

/* Main container for handling the login screen, handles the login and
registration fragments of the log in functionality */

class ReminderActivity : AppCompatActivity() {

    //private lateinit var database:Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        //database = Database(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.reminder_fragment_container, ReminderFragment())
                .commitNow()
        }
    }






//    override fun onDestroy() {
////        database.close()
//        super.onDestroy()
//    }

//    //Database CRUD helper methods
//    fun addUser(username: String, pass: String) {
//        val db = database.writableDatabase
//        db.execSQL("INSERT INTO users (username, pass) VALUES ('$username', '$pass')")
//        db.close()
//    }
//
//    fun addReminder(description: String, dateTime: String, locationId: Int, userId: Int, taskId: Int) {
//        val db = database.writableDatabase
//        db.execSQL("INSERT INTO reminders (description, date_time, location_id, user_id, task_id) VALUES ('$description', '$dateTime', $locationId, $userId, $taskId)")
//        db.close()
//    }
//
//    fun removeReminder(reminderId: Int) {
//        val db = database.writableDatabase
//        db.execSQL("DELETE FROM reminders WHERE reminder_id = $reminderId")
//        db.close()
//    }

}


