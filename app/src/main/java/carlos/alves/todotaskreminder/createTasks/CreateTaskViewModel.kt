package carlos.alves.todotaskreminder.createTasks

import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
import carlos.alves.todotaskreminder.notifications.DateReminderService
import carlos.alves.todotaskreminder.notifications.LocationReminderService
import carlos.alves.todotaskreminder.settings.SettingsConstants.*
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskInfo
import carlos.alves.todotaskreminder.sharedTasks.SharedTasksServer
import carlos.alves.todotaskreminder.utilities.DateTimeJson
import carlos.alves.todotaskreminder.utilities.JsonConverter
import java.time.LocalDate
import java.time.LocalTime

class CreateTaskViewModel : ViewModel() {

    var name: String? = null
    var description: String? = null
    var remindByLocation: Boolean = false
    var remindByDate: Boolean = false
    var distanceReminder: Double = 0.0
    var dateReminder: LocalDate? = null
    var timeReminder: LocalTime? = null
    val locationsId = mutableListOf<Int>()
    var uploadToCloud: Boolean = false
    var userPassword: String? = null
    var adminPassword: String? = null

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository
    private val dateReminderService = DateReminderService.instance
    private val locationReminderService = LocationReminderService.instance
    private val sharedTasksServer = SharedTasksServer.instance
    private lateinit var calendar: Calendar

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun checkIfTaskNameAlreadyExists(): Boolean = taskRepository.getTask(name!!) != null

    fun checkIfPasswordsNotOk(): Boolean {
        return userPassword.isNullOrBlank() || adminPassword.isNullOrBlank()
    }

    fun createTask(context: Context) {
        taskRepository.insertNewTask(TaskEntity(
            0,
            name!!,
            description!!,
            false,
            remindByLocation,
            remindByDate)
        )

        val taskId = taskRepository.getTaskId(name!!)

        if (remindByDate) {
            dateTimeRepository.insertDateTime(DateTimeEntity(
                taskId,
                dateReminder!!,
                timeReminder!!)
            )
            dateReminderService.setDateToRemind(context, taskId, name!!, calendar)
        }

        if (remindByLocation) {
            locationsId.forEach {
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

        if (uploadToCloud) {
            val sharedTaskInfo = generateSharedTaskInfo(taskId)
            sharedTasksServer.storeSharedTaskOnCloud(context, taskId, sharedTaskInfo)
        }
    }

    fun dateTimeAlreadyPassed(): Boolean {
        calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis
        calendar.set(dateReminder!!.year, dateReminder!!.monthValue - 1, dateReminder!!.dayOfMonth, timeReminder!!.hour, timeReminder!!.minute, 0)
        val alarmDate = calendar.timeInMillis
        return currentDate >= alarmDate
    }

    private fun generateSharedTaskInfo(taskId: Int): SharedTaskInfo {
        val onLocations = onLocationRepository.getOnLocations(taskId)
        val locations = onLocations.map { locationRepository.getLocationById(it.locationId) }

        val sharedTaskInfo = SharedTaskInfo()
        sharedTaskInfo.task = JsonConverter.convertTaskToJsonTask(taskRepository.getTask(taskId))
        sharedTaskInfo.dateTime = if (remindByDate) JsonConverter.convertDateTimeToJsonDateTime(DateTimeJson.generateDateTimeJson(dateTimeRepository.getDateTime(taskId))) else null
        sharedTaskInfo.locations = if (remindByLocation) locations.map { JsonConverter.convertLocationToJsonLocation(it) } else null
        sharedTaskInfo.onLocations = if (remindByLocation) onLocations.map { JsonConverter.convertOnLocationToJsonOnLocation(it) } else null
        sharedTaskInfo.userPassword = userPassword!!
        sharedTaskInfo.adminPassword = adminPassword!!

        return sharedTaskInfo
    }
}