package carlos.alves.todotaskreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import carlos.alves.todotaskreminder.repository.TasksDatabase
import carlos.alves.todotaskreminder.repository.TasksRepository

object TasksApp : Application() {

    private const val TASKS_CHANNEL_ID = "tasks.channel"

    private val database by lazy {
        Room.databaseBuilder(applicationContext, TasksDatabase::class.java, "tasks_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val repository by lazy {
        TasksRepository(database)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupNotificationChannel() {

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(TASKS_CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(TASKS_CHANNEL_ID, "Tasks Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}