//package com.example.locationbasedreminders.database
//
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import kotlin.system.exitProcess
//
//class Database(context: Context) : SQLiteOpenHelper(context, "LOCATION_BASED_REMINDERS_DATABASE", null, 1) {
//    override fun onCreate(p0: SQLiteDatabase) {
//        p0.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))")
//        p0.execSQL("CREATE TABLE locations (location_id INTEGER PRIMARY KEY, zone_name VARCHAR(255), latitude REAL, longitude REAL)")
//        p0.execSQL("CREATE TABLE tasks (task_id INTEGER PRIMARY KEY, task_description VARCHAR(255))")
//        p0.execSQL("CREATE TABLE reminders (reminder_id INTEGER PRIMARY KEY, description VARCHAR(255), date_time VARCHAR(255), location_id INTEGER, FOREIGN KEY(location_id) REFERENCES locations(location_id), FOREIGN KEY(user_id) REFERENCES users(user_id), FOREIGN KEY(task_id) references tasks(task_id))")
//    }
//
//    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
//        print("Not needed")
//        exitProcess(1)
//    }
//
//}