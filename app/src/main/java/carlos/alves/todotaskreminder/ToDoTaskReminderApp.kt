package carlos.alves.todotaskreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.notifications.DateReminderService
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import carlos.alves.todotaskreminder.repository.*
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun renewDateReminders() {
        val remindByDateTasks = taskRepository.getAllRemindByDateTasks().filter { !it.completed }
        remindByDateTasks.forEach {
            val dateTime = dateTimeRepository.getDateTime(it.id)
            val calendar = Calendar.getInstance()
            if (!dateTimeAlreadyPassed(calendar, dateTime)) {
                DateReminderService.instance.setDateToRemind(applicationContext, it.id, it.name, calendar)
            }
        }
    }

    private fun dateTimeAlreadyPassed(calendar: Calendar, dateTime: DateTimeEntity): Boolean {
        val currentDate = calendar.timeInMillis
        calendar.set(dateTime.date.year, dateTime.date.monthValue - 1, dateTime.date.dayOfMonth, dateTime.time.hour, dateTime.time.minute, 0)
        val alarmDate = calendar.timeInMillis
        return currentDate >= alarmDate
    }
}