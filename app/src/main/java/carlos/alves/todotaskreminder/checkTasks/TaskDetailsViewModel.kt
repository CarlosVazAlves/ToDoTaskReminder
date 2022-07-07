package carlos.alves.todotaskreminder.checkTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity

class TaskDetailsViewModel : ViewModel() {
    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository

    private lateinit var task: TaskEntity
    private var dateTime: DateTimeEntity? = null
    private var onLocation: ArrayList<OnLocationEntity>? = null

    fun fetchTask(taskName: String): TaskEntity {
        task = taskRepository.getTask(taskName)!!
        return task
    }

    fun fetchLocationName(locationId: Int): String = locationRepository.getLocationById(locationId).name

    fun fetchOnLocations(): ArrayList<OnLocationEntity> {
        if (onLocation == null) {
            onLocation = onLocationRepository.getOnLocations(task.id)
        }
        return onLocation!!
    }

    fun fetchOnlineTaskId(): Int? {
        val onlineTask = onlineTaskRepository.getOnlineTaskByTaskId(task.id)
        return onlineTask?.onlineTaskId
    }

    fun getDistance(): Double = onLocation!!.first().distance

    fun fetchDateTime(): DateTimeEntity {
        if (dateTime == null) {
            dateTime = dateTimeRepository.getDateTime(task.id)
        }
        return dateTime!!
    }
}