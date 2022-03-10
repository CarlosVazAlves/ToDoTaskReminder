package carlos.alves.todotaskreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import carlos.alves.todotaskreminder.repository.*

object ToDoTaskReminderApp : Application() {

    private const val TASKS_CHANNEL_ID = "toDoTaskReminder.channel"

    private val database by lazy {
        Room.databaseBuilder(applicationContext, ToDoTaskReminderDatabase::class.java, "toDoTaskReminder_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val taskRepository by lazy {
        TaskRepository(database)
    }

    val dateTimeRepository by lazy {
        DateTimeRepository(database)
    }

    val locationRepository by lazy {
        LocationRepository(database)
    }

    val onLocationRepository by lazy {
        OnLocationRepository(database)
    }

    val settingsRepository by lazy {
        SettingsRepository(database)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupNotificationChannel() {

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(TASKS_CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(TASKS_CHANNEL_ID, "ToDo Task Reminder Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}