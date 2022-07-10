package carlos.alves.todotaskreminder.deleteTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.TaskObject
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class DeleteTasksListViewModel : ViewModel()  {

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val dateTimeRepository = ToDoTaskReminderApp.instance.dateTimeRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    private lateinit var tasksNames: ArrayList<String>

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

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