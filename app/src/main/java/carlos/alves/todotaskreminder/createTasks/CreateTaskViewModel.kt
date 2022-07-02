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

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val dateReminderService = DateReminderService.instance
    private val locationReminderService = LocationReminderService.instance
    private lateinit var calendar: Calendar

    fun checkIfTaskNameAlreadyExists(): Boolean = taskRepository.getTask(name!!) != null

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
    }

    fun dateTimeAlreadyPassed(): Boolean {
        calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis
        calendar.set(dateReminder!!.year, dateReminder!!.monthValue - 1, dateReminder!!.dayOfMonth, timeReminder!!.hour, timeReminder!!.minute, 0)
        val alarmDate = calendar.timeInMillis
        return currentDate >= alarmDate
    }
}