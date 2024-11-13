package com.example.locationbasedreminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.locationbasedreminders.activity.ReminderActivity
import com.example.locationbasedreminders.model.AccountViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorMessage = geofencingEvent?.errorCode?.let {
                GeofenceStatusCodes.getStatusCodeString(it)
            } ?: "Unknown error"
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "Geofence transition detected: $geofenceTransition")

            if (!triggeringGeofences.isNullOrEmpty()) {
                for (geofence in triggeringGeofences) {
                    Log.d(TAG, "Triggered geofence: ${geofence.requestId}")
                    val geofenceId = geofence.requestId
                    val accountViewModel = AccountViewModel()

                    accountViewModel.getReminderByGeofenceId(geofenceId) { reminder ->
                        if (reminder != null) {
                            sendNotification(context, reminder.name, reminder.userID)
                        } else {
                            Log.d(TAG, "No reminder found for geofence: $geofenceId")
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "Invalid geofence transition type: $geofenceTransition")
        }
    }

    private fun sendNotification(context: Context, reminderTitle: String, reminderId: Int) {
        // Register with app's notification channel
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = "geofence_channel"
        val channel = NotificationChannel(
            notificationChannelId,
            "Geofence Notifications",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, ReminderActivity::class.java)

        // Create a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle("Location Based Reminder Alert")
            .setContentText(reminderTitle)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Create unique value for notificationId
        val notificationId = reminderId.hashCode()

        // Send to device
        notificationManager.notify(notificationId, notification)
    }
}
