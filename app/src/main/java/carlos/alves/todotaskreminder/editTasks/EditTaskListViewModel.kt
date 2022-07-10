package carlos.alves.todotaskreminder.editTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class EditTaskListViewModel : ViewModel() {
    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    private lateinit var tasksNames: ArrayList<String>

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun fetchTasksNames(): ArrayList<String> {
        tasksNames = taskRepository.getAllTaskNames()
        return tasksNames
    }
}