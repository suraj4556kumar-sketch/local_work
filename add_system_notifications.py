import os
import re

manifest_path = 'app/src/main/AndroidManifest.xml'
with open(manifest_path, 'r', encoding='utf-8') as f:
    manifest_content = f.read()

if 'POST_NOTIFICATIONS' not in manifest_content:
    manifest_content = manifest_content.replace('<application', '<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />\n    <application')
    with open(manifest_path, 'w', encoding='utf-8') as f:
        f.write(manifest_content)

notification_helper_code = """package com.example.localconnect.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.localconnect.MainActivity
import com.example.localconnect.R

object NotificationHelper {
    private const val CHANNEL_ID = "localconnect_notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "LocalConnect Notifications"
            val descriptionText = "Notifications for jobs and services"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Make sure this exists or use android.R.drawable.ic_dialog_info
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
"""

os.makedirs('app/src/main/java/com/example/localconnect/utils', exist_ok=True)
with open('app/src/main/java/com/example/localconnect/utils/NotificationHelper.kt', 'w', encoding='utf-8') as f:
    f.write(notification_helper_code)


homescreen_path = 'app/src/main/java/com/example/localconnect/HomeScreen.kt'
with open(homescreen_path, 'r', encoding='utf-8') as f:
    homescreen_content = f.read()

new_launched_effect = """
    val context = androidx.compose.ui.platform.LocalContext.current
    var previousNotificationCount by remember { mutableStateOf(-1) }

    LaunchedEffect(notifications) {
        if (previousNotificationCount != -1 && notifications.size > previousNotificationCount) {
            // A new notification was added!
            val latestNotification = notifications.firstOrNull() // assuming sorted descending by timestamp
            if (latestNotification != null && !latestNotification.isRead) {
                com.example.localconnect.utils.NotificationHelper.showNotification(context, latestNotification.title, latestNotification.message)
            }
        }
        previousNotificationCount = notifications.size
    }
"""

if 'previousNotificationCount' not in homescreen_content:
    # insert it right after al unreadCount = notifications.count { !it.isRead }
    target = 'val unreadCount = notifications.count { !it.isRead }'
    homescreen_content = homescreen_content.replace(target, target + '\n' + new_launched_effect)
    with open(homescreen_path, 'w', encoding='utf-8') as f:
        f.write(homescreen_content)

print("Added System Notifications feature")
