package carlos.alves.todotaskreminder.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.icu.util.Calendar
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.AdapterConstants
import carlos.alves.todotaskreminder.editTasks.EditTaskActivity
import carlos.alves.todotaskreminder.notifications.NotificationConstants.*
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class DateReminderService : BroadcastReceiver() {

    private val permissions = PermissionsUtility.instance

    init {
        instance = this
    }

    companion object {
        lateinit var instance: DateReminderService
            private set
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent!!.getIntExtra(TASK_ID.description, 0)

        val editTaskIntent = Intent(context, EditTaskActivity::class.java).let {
            it.putExtra(AdapterConstants.CHOSEN_TASK.description, intent.getStringExtra(TASK_NAME.description))
            it.putExtra(AdapterConstants.ACTIVE_DATE_NOTIFICATION.description, true)
            PendingIntent.getActivity(context, taskId, it, permissions.getPendingIntentMutabilityFlag())
        }

        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.date_reminder))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(context, 0, Intent(), permissions.getPendingIntentMutabilityFlag()))
            .addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.edit_task), editTaskIntent).build())

        val message = context.getString(R.string.it_is_time_of_task, intent.getStringExtra(TASK_NAME.description))
        notificationManager.notify(taskId, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }

    fun setDateToRemind(context: Context, taskId: Int, taskName: String, calendar: Calendar) {
        if (permissions.checkScheduleExactAlarmPermissionOk()) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, DateReminderService::class.java).let {
                it.putExtra(TASK_NAME.description, taskName)
                it.putExtra(TASK_ID.description, taskId)
                PendingIntent.getBroadcast(context, taskId, it, permissions.getPendingIntentMutabilityFlag()) //Android 12 exige flag
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
            PendingIntent.getBroadcast(context, taskId, it, permissions.getPendingIntentMutabilityFlag()) //Android 12 exige flag
        }
        alarmManager.cancel(alarmIntent)
    }

    fun removeNotification(context: Context, taskId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId)
    }
}
