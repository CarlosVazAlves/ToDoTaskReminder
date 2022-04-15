package carlos.alves.todotaskreminder.createTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.DateTimeEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
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
    val locationsIds = mutableListOf<Int>()

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository

    fun checkIfTaskNameAlreadyExists(): Boolean = taskRepository.getTask(name!!) != null

    fun createTask() {
        taskRepository.insertNewTask(TaskEntity(
            0,
            name!!,
            description!!,
            false,
            remindByLocation,
            remindByDate
        ))

        val taskId = taskRepository.getTaskId(name!!)

        if (remindByDate) {
            dateTimeRepository.insertDateTime(DateTimeEntity(
                taskId,
                dateReminder!!,
                timeReminder!!
            ))
        }

        if (remindByLocation) {
            locationsIds.forEach {
                onLocationRepository.insertOnLocation(OnLocationEntity(
                    taskId,
                    it,
                    distanceReminder
                ))
            }
        }
    }
}