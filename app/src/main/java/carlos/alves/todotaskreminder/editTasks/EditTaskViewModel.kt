package carlos.alves.todotaskreminder.editTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
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

    fun checkIfTaskNameAlreadyExists(): Boolean {
        if (initialName == task.name) return false
        return taskRepository.getTask(task.name) != null
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

    fun editTask() {
        taskRepository.updateTask(task)

        if (task.remindByDate) {
            updateDateReminder()
        } else {
            dateTimeRepository.deleteDateTime(task.id)
        }

        if (task.remindByLocation) {
            updateLocationReminder()
        } else {
            onLocationRepository.deleteOnLocationsByTaskId(task.id)
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

    private fun updateLocationReminder() {
        if (taskLocationReminders != null) {
            val locationIdsPreSelected = taskLocationReminders!!.map { it.locationId }
            val locationIdsToAdd = locationsId.filter { !locationIdsPreSelected.contains(it) }
            val locationIdsToRemove = locationIdsPreSelected.filter { !locationsId.contains(it) }

            locationIdsToRemove.forEach {
                onLocationRepository.deleteOnLocationsByLocationId(it)
            }

            taskLocationReminders!!.forEach {
                it.distance = distanceReminder
                onLocationRepository.updateOnLocation(it)
            }

            locationIdsToAdd.forEach {
                onLocationRepository.insertOnLocation(
                    OnLocationEntity(
                        task.id,
                        it,
                        distanceReminder)
                )
            }
        } else {
            locationsId.forEach {
                onLocationRepository.insertOnLocation(
                    OnLocationEntity(
                        task.id,
                        it,
                        distanceReminder)
                )
            }
        }
    }
}