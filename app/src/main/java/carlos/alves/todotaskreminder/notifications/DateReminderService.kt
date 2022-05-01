package carlos.alves.todotaskreminder.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.notifications.NotificationConstants.TASK_NAME

class DateReminderService : BroadcastReceiver() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: DateReminderService
            private set
    }

    fun setDateToRemind(context: Context, taskId: Int, taskName: String, calendar: Calendar) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, DateReminderService::class.java).let { intent ->
            intent.putExtra(TASK_NAME.description, taskName)
            PendingIntent.getBroadcast(context, taskId, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_MUTABLE else FLAG_UPDATE_CURRENT) //Android 12 exige flag
        }

        alarmManager.setExactAndAllowWhileIdle( //Alarme inexacto não toca a horas certas
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    fun removeDateToRemind(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, DateReminderService::class.java).let { intent ->
            PendingIntent.getBroadcast(context, taskId, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_MUTABLE else FLAG_UPDATE_CURRENT) //Android 12 exige flag
        }

        alarmManager.cancel(alarmIntent)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.date_reminder))

        val message = context.getString(R.string.it_is_time_of_task, intent!!.getStringExtra(TASK_NAME.description))
        notificationManager.notify(0, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }
}
