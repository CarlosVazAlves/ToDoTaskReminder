package carlos.alves.todotaskreminder.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity

class RebootHandler : BroadcastReceiver() {

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val dateReminderService = DateReminderService.instance
    private val locationReminderService = LocationReminderService.instance

    override fun onReceive(context: Context?, intent: Intent?) {
        renewDateReminders(context!!)
        renewGeofenceLocations(context)
    }

    private fun renewGeofenceLocations(context: Context) {
        val remindByLocationTaskIds = taskRepository.getAllRemindByLocationTasks().filter { !it.completed }.map { it.id }
        remindByLocationTaskIds.forEach { taskId ->
            val onLocationsListByTask = onLocationRepository.getOnLocations(taskId)
            onLocationsListByTask.forEach { onLocation ->
                val location = locationRepository.getLocationById(onLocation.locationId)
                val geofenceId = "$taskId:${location.name}"
                locationReminderService.addLocationToGeoFence(context, geofenceId, location, onLocation.distance)
            }
        }
    }

    private fun renewDateReminders(context: Context) {
        val remindByDateTasks = taskRepository.getAllRemindByDateTasks().filter { !it.completed }
        remindByDateTasks.forEach {
            val dateTime = dateTimeRepository.getDateTime(it.id)
            val calendar = Calendar.getInstance()
            if (!dateTimeAlreadyPassed(calendar, dateTime)) {
                dateReminderService.setDateToRemind(context, it.id, it.name, calendar)
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