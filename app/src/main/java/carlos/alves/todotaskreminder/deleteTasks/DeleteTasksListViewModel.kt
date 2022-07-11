package carlos.alves.todotaskreminder.deleteTasks

import android.content.Context
import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.TaskObject
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class DeleteTasksListViewModel : ViewModel()  {

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val locationReminderService = LocationReminderService.instance

    private lateinit var taskNames: ArrayList<String>

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun fetchTaskNames() {
        taskNames = taskRepository.getAllTaskNames()
    }

    fun generateTaskObjectList() = ArrayList(taskNames.map { TaskObject(it) })

    fun deleteTask(context: Context, taskName: String) {
        val taskId = taskRepository.getTaskId(taskName)
        removeFromOnLocationsRepository(context, taskId)
        dateTimeRepository.deleteDateTime(taskId)
        onlineTaskRepository.deleteOnlineTaskByTaskId(taskId)
        taskRepository.deleteTask(taskId)
    }

    private fun removeFromOnLocationsRepository(context: Context, taskId: Int) {
        val onLocations = onLocationRepository.getOnLocations(taskId)
        if (onLocations.isEmpty()) { return }
        onLocationRepository.deleteOnLocationsByTaskId(taskId)
        onLocations.forEach { removeFromGeoFence(context, taskId, it.locationId) }
    }

    private fun removeFromGeoFence(context: Context, taskId: Int, locationId: Int) {
        val location = locationRepository.getLocationById(locationId)
        val geofenceId = "$taskId:${location.name}"
        locationReminderService.removeLocationFromGeofence(context, geofenceId)
    }
}