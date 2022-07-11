package carlos.alves.todotaskreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.room.Room
import carlos.alves.todotaskreminder.database.SettingsEntity
import carlos.alves.todotaskreminder.notifications.DateReminderService
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import carlos.alves.todotaskreminder.repository.*
import carlos.alves.todotaskreminder.settings.SettingsConstants.*
import carlos.alves.todotaskreminder.sharedTasks.SharedTasksServer
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class ToDoTaskReminderApp : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: ToDoTaskReminderApp
            private set
        const val TASKS_CHANNEL_ID = "toDoTaskReminder.channel"
    }

    val database by lazy { Room.databaseBuilder(applicationContext, ToDoTaskReminderDatabase::class.java, "toDoTaskReminder_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val taskRepository by lazy {
        TaskRepository(database)
    }

    val onlineTaskRepository by lazy {
        OnlineTaskRepository(database)
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

    override fun onCreate() {
        super.onCreate()
        PermissionsUtility(applicationContext)
        DateReminderService()
        LocationReminderService()
        SharedTasksServer()
        firstSettingsSetup()
    }

    fun setupNotificationChannel() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(TASKS_CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                TASKS_CHANNEL_ID,
                "ToDo Task Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun firstSettingsSetup() {
        if (settingsRepository.getAllSettings().isEmpty()) {
            settingsRepository.insertSetting(SettingsEntity(BUTTONS_COLOR.description, Color.rgb(64, 101, 150).toString()))
            settingsRepository.insertSetting(SettingsEntity(BACKGROUND_COLOR.description, Color.rgb(255, 255, 255).toString()))
        }
    }
}