package com.example.locationbasedreminders.reminder
import java.time.*
import java.time.format.DateTimeFormatter

data class Reminder(
    var time: Date,
    var location: Location,
    var description: String)


data class Date(var day: String, var hour: Int, var minute: Int)

data class Location(var lat: Float, var long: Float)