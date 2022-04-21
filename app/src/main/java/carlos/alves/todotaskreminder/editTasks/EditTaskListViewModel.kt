package carlos.alves.todotaskreminder.editTasks

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp

class EditTaskListViewModel : ViewModel() {
    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository

    private lateinit var tasksNames: ArrayList<String>

    fun fetchTasksNames(): ArrayList<String> {
        tasksNames = taskRepository.getAllTaskNames()
        return tasksNames
    }
}