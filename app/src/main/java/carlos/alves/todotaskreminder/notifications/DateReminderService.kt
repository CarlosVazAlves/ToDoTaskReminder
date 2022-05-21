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
import android.graphics.drawable.Icon
import android.icu.util.Calendar
import android.os.Build
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.AdapterConstants
import carlos.alves.todotaskreminder.editTasks.EditTaskActivity
import carlos.alves.todotaskreminder.notifications.NotificationConstants.*
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class DateReminderService : BroadcastReceiver() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: DateReminderService
            private set
    }

    fun setDateToRemind(context: Context, taskId: Int, taskName: String, calendar: Calendar) {
        val permissions = PermissionsUtility.instance

        if (permissions.checkScheduleExactAlarmPermissionOk()) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, DateReminderService::class.java).let {
                it.putExtra(TASK_NAME.description, taskName)
                it.putExtra(TASK_ID.description, taskId)
                PendingIntent.getBroadcast(context, taskId, it, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_MUTABLE else FLAG_UPDATE_CURRENT) //Android 12 exige flag
            }

            alarmManager.setExactAndAllowWhileIdle( //Alarme inexacto não toca a horas certas
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }
    }

    fun removeDateToRemind(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, DateReminderService::class.java).let {
            PendingIntent.getBroadcast(context, taskId, it, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_MUTABLE else FLAG_UPDATE_CURRENT) //Android 12 exige flag
        }
        alarmManager.cancel(alarmIntent)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent!!.getIntExtra(TASK_ID.description, 0)

        val editTaskIntent = Intent(context, EditTaskActivity::class.java).let {
            it.putExtra(AdapterConstants.CHOSEN_TASK.description, intent.getStringExtra(TASK_NAME.description))
            PendingIntent.getActivity(context, taskId, it, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_MUTABLE else FLAG_UPDATE_CURRENT)
        }

        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.date_reminder))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(context, 0, Intent(), 0))
            .addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.edit_task), editTaskIntent).build())

        val message = context.getString(R.string.it_is_time_of_task, intent.getStringExtra(TASK_NAME.description))
        notificationManager.notify(taskId, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }
}
