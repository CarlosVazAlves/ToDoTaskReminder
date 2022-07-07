package carlos.alves.todotaskreminder.deleteTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.TaskObject

class DeleteTasksListViewModel : ViewModel()  {

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository

    private lateinit var tasksNames: ArrayList<String>

    fun fetchTasksNames() {
        tasksNames = taskRepository.getAllTaskNames()
    }

    fun generateTaskObjectList() = ArrayList(tasksNames.map { TaskObject(it) })

    fun deleteTask(taskName: String) {
        val taskId = taskRepository.getTaskId(taskName)
        onLocationRepository.deleteOnLocationsByTaskId(taskId)
        dateTimeRepository.deleteDateTime(taskId)
        onlineTaskRepository.deleteOnlineTaskByTaskId(taskId)
        taskRepository.deleteTask(taskId)
    }
}