package carlos.alves.todotaskreminder.sharedTasks

import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.*
import carlos.alves.todotaskreminder.notifications.DateReminderService
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import carlos.alves.todotaskreminder.repository.SharedTasksRepository
import carlos.alves.todotaskreminder.utilities.JsonConverter
import carlos.alves.todotaskreminder.settings.SettingsConstants.*
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter
import carlos.alves.todotaskreminder.utilities.LocationUtility

import java.time.LocalDate
import java.time.LocalTime

class SharedTaskViewModel : ViewModel()  {

    lateinit var sharedTaskInfo: SharedTaskInfo
    lateinit var name: String
    lateinit var description: String
    lateinit var onlineTaskId: String
    var remindByLocation: Boolean = false
    var remindByDate: Boolean = false
    var completed: Boolean = false
    var distanceReminder: Double = 0.0
    var dateReminder: LocalDate? = null
    var timeReminder: LocalTime? = null
    val locations = mutableListOf<LocationEntity>()
    private val onLocations = mutableListOf<OnLocationEntity>()

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository
    private val sharedTasksRepository = SharedTasksRepository.instance
    private val dateReminderService = DateReminderService.instance
    private val locationReminderService = LocationReminderService.instance

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun populateFields() {
        val sharedTask = JsonConverter.convertJsonTaskToTask(sharedTaskInfo.task)
        name = sharedTask.name
        description = sharedTask.description
        remindByDate = sharedTask.remindByDate
        remindByLocation = sharedTask.remindByLocation
        completed = sharedTask.completed

        if (remindByDate) {
            populateDateTime()
        }

        if (remindByLocation) {
            populateLocations()
        }
    }

    fun checkIfLocationNameExists(): Boolean {
        val existingLocations = locationRepository.getLocations()
        val existingLocationNames = existingLocations.map { it.name }
        val locationNames = locations.map { it.name }
        val potentialLocationNamesToRemove = mutableListOf<String>()

        existingLocationNames.forEach {
            if (locationNames.contains(it)) {
                potentialLocationNamesToRemove.add(it)
            }
        }

        val potentialLocationsToRemove = locations.filter { potentialLocationNamesToRemove.contains(it.name) }.toMutableList()
        if (potentialLocationsToRemove.isEmpty()) {
            return false
        }

        potentialLocationsToRemove.forEach { potentialLocationToRemove ->
            val existingLocation = existingLocations.single { it.name == potentialLocationToRemove.name }
            val existingLocationCoordinates = CoordinatesConverter.convertStringToLatLng(existingLocation.coordinates)!!
            val potentialLocationToRemoveCoordinates = CoordinatesConverter.convertStringToLatLng(potentialLocationToRemove.coordinates)!!
            val isToRemove = LocationUtility.isWithinDistance(existingLocationCoordinates, potentialLocationToRemoveCoordinates)

            if (isToRemove) {
                locations.remove(potentialLocationToRemove)
                locations.add(existingLocation)
                potentialLocationsToRemove.remove(potentialLocationToRemove)
            }
        }

        return potentialLocationsToRemove.isNotEmpty()
    }

    fun checkIfTaskAlreadyDownloaded(): Boolean {
        val existingOnlineTask = onlineTaskRepository.getOnlineTaskByOnlineTaskId(onlineTaskId.toInt())
        return existingOnlineTask != null
    }

    fun checkIfTaskNameExists(): Boolean {
        val existingTask = taskRepository.getTask(name)
        return existingTask != null
    }

    fun storeInLocalDataBase(context: Context) {
        taskRepository.insertNewTask(
            TaskEntity(
                0,
                name,
                description,
                completed,
                remindByLocation,
                remindByDate
            )
        )

        val taskId = taskRepository.getTaskId(name)

        onlineTaskRepository.insertOnlineTask(OnlineTaskEntity(taskId, onlineTaskId.toInt()))

        if (remindByDate) {
            storeDateTimeInLocalDataBase(context, taskId)
        }

        if (remindByLocation) {
            storeLocationsInLocalDataBase(context, taskId)
        }
    }

    fun deleteOnlineTask() = sharedTasksRepository.deleteOnlineTaskFromCloud(onlineTaskId)

    private fun populateDateTime() {
        val sharedDateTime = JsonConverter.convertJsonDateTimeToDateTime(sharedTaskInfo.dateTime!!).convertToDateTimeEntity()
        dateReminder = sharedDateTime.date
        timeReminder = sharedDateTime.time
    }

    private fun populateLocations() {
        val sharedLocations = sharedTaskInfo.locations?.map { JsonConverter.convertJsonLocationToLocation(it) }!!
        sharedLocations.forEach { locations.add(it) }

        val sharedOnLocations = sharedTaskInfo.onLocations?.map { JsonConverter.convertJsonOnLocationToOnLocation(it) }!!
        sharedOnLocations.forEach { onLocations.add(it) }

        distanceReminder = onLocations.first().distance
    }

    private fun storeDateTimeInLocalDataBase(context: Context, taskId: Int) {
        dateTimeRepository.insertDateTime(
            DateTimeEntity(
                taskId,
                dateReminder!!,
                timeReminder!!)
        )

        if (!completed) {
            val calendar = Calendar.getInstance()
            calendar.set(dateReminder!!.year, dateReminder!!.monthValue - 1, dateReminder!!.dayOfMonth, timeReminder!!.hour, timeReminder!!.minute, 0)
            dateReminderService.setDateToRemind(context, taskId, name, calendar)
        }
    }

    private fun storeLocationsInLocalDataBase(context: Context, taskId: Int) {
        storeLocations()
        val newLocationNames = locations.map { it.name }
        val newLocationIds = mutableListOf<Int>()
        newLocationNames.forEach {
            newLocationIds.add(locationRepository.getLocationIdByName(it))
        }

        newLocationIds.forEach {
            onLocationRepository.insertOnLocation(OnLocationEntity(
                taskId,
                it,
                distanceReminder)
            )

            val location = locationRepository.getLocationById(it)
            val geofenceId = "$taskId:${location.name}"
            locationReminderService.addLocationToGeoFence(context, geofenceId, location, distanceReminder)
        }
    }

    private fun storeLocations() {
        locations.forEach {
            locationRepository.insertLocation(
                LocationEntity(
                    0,
                    it.name,
                    it.address,
                    it.group,
                    it.coordinates
                )
            )
        }
    }
}