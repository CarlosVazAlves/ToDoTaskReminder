package carlos.alves.todotaskreminder.editTasks

import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
import carlos.alves.todotaskreminder.notifications.DateReminderService
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import java.time.LocalDate
import java.time.LocalTime

class EditTaskViewModel : ViewModel() {

    lateinit var task: TaskEntity
    lateinit var initialName: String
    private var taskDateReminder: DateTimeEntity? = null
    private var taskLocationReminders: ArrayList<OnLocationEntity>? = null
    var distanceReminder: Double = 0.0
    var dateReminder: LocalDate? = null
    var timeReminder: LocalTime? = null
    val locationsId = mutableListOf<Int>()

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val dateReminderService = DateReminderService.instance
    private val locationReminderService = LocationReminderService.instance
    private lateinit var calendar: Calendar

    fun checkIfTaskNameAlreadyExists(): Boolean {
        if (initialName == task.name) return false
        return taskRepository.getTask(task.name) != null
    }

    fun isTaskCompleted(): Boolean = task.completed

    fun dateTimeAlreadyPassed(): Boolean {
        calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis
        calendar.set(dateReminder!!.year, dateReminder!!.monthValue - 1, dateReminder!!.dayOfMonth, timeReminder!!.hour, timeReminder!!.minute, 0)
        val alarmDate = calendar.timeInMillis
        return currentDate >= alarmDate
    }

    fun loadTask(taskName: String) {
        task = taskRepository.getTask(taskName)!!
        initialName = task.name

        if (task.remindByDate) {
            taskDateReminder = dateTimeRepository.getDateTime(task.id)
            dateReminder = taskDateReminder!!.date
            timeReminder = taskDateReminder!!.time
        }
        if (task.remindByLocation) {
            taskLocationReminders = onLocationRepository.getOnLocations(task.id)
            distanceReminder = taskLocationReminders!!.first().distance
            taskLocationReminders!!.forEach { locationsId.add(it.locationId) }
        }
    }

    fun editTask(context: Context) {
        taskRepository.updateTask(task)
        val taskId = task.id

        if (task.remindByDate) {
            updateDateReminder()

            if (task.completed) {
                dateReminderService.removeDateToRemind(context, taskId)
            } else {
                dateReminderService.setDateToRemind(context, taskId, task.name, calendar)
            }
        } else {
            dateTimeRepository.deleteDateTime(taskId)
            dateReminderService.removeDateToRemind(context, taskId)
        }

        if (task.remindByLocation) {
            updateLocationReminder(context)
        } else {
            if (locationsId.isNotEmpty()) {
                locationsId.forEach { removeFromGeoFence(context, taskId, it) }
                onLocationRepository.deleteOnLocationsByTaskId(taskId)
            }
        }
    }

    private fun updateDateReminder() {
        if (taskDateReminder != null) {
            taskDateReminder!!.date = dateReminder!!
            taskDateReminder!!.time = timeReminder!!
            dateTimeRepository.updateDateTime(taskDateReminder!!)
        } else {
            dateTimeRepository.insertDateTime(DateTimeEntity(task.id, dateReminder!!, timeReminder!!))
        }
    }

    private fun updateLocationReminder(context: Context) {
        val taskId = task.id

        if (taskLocationReminders != null) {
            val locationIdsPreSelected = taskLocationReminders!!.map { it.locationId }
            val locationIdsToAdd = locationsId.filter { !locationIdsPreSelected.contains(it) }
            val locationIdsToRemove = locationIdsPreSelected.filter { !locationsId.contains(it) }

            taskLocationReminders!!.forEach {
                it.distance = distanceReminder
                onLocationRepository.updateOnLocation(it)
            }

            if (locationIdsToAdd.isEmpty() && locationIdsToRemove.isEmpty()) {
                if (task.completed) {
                    locationsId.forEach { removeFromGeoFence(context, taskId, it) }
                } else {
                    locationIdsPreSelected.forEach { addToGeoFence(context, taskId, it) }
                }
                return
            }

            locationIdsToRemove.forEach {
                onLocationRepository.deleteOnLocationsByLocationId(it)
                if (!task.completed) {
                    removeFromGeoFence(context, taskId, it)
                }
            }

            locationIdsToAdd.forEach {
                onLocationRepository.insertOnLocation(
                    OnLocationEntity(
                        taskId,
                        it,
                        distanceReminder)
                )
                if (!task.completed) {
                    addToGeoFence(context, taskId, it)
                }
            }

            if (task.completed) {
                locationsId.forEach { removeFromGeoFence(context, taskId, it) }
            }
        } else {
            locationsId.forEach {
                onLocationRepository.insertOnLocation(
                    OnLocationEntity(
                        taskId,
                        it,
                        distanceReminder)
                )
                if (!task.completed) {
                    addToGeoFence(context, taskId, it)
                }
            }
        }
    }

    private fun addToGeoFence(context: Context, taskId: Int, locationId: Int) {
        val location = locationRepository.getLocationById(locationId)
        val geofenceId = "$taskId:${location.name}"
        locationReminderService.addLocationToGeoFence(context, geofenceId, location, distanceReminder)
    }

    private fun removeFromGeoFence(context: Context, taskId: Int, locationId: Int) {
        val location = locationRepository.getLocationById(locationId)
        val geofenceId = "$taskId:${location.name}"
        locationReminderService.removeLocationFromGeofence(context, geofenceId)
    }
}