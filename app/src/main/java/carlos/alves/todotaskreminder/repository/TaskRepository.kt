package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.TaskEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TaskRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val taskDatabaseDao = database.taskDatabaseDao()

    fun insertNewTask(task: TaskEntity) { executor.submit { taskDatabaseDao.insertTask(task) } }

    fun updateTask(task: TaskEntity) { executor.submit { taskDatabaseDao.updateTask(task) } }

    fun deleteTask(taskName: String) { executor.submit { taskDatabaseDao.deleteTaskByName(taskName) } }

    fun getTask(taskId: Int): TaskEntity = executor.submit(Callable { taskDatabaseDao.getTaskById(taskId) }).get()

    fun getTask(taskName: String): TaskEntity = executor.submit(Callable { taskDatabaseDao.getTaskByName(taskName) }).get()

    fun getAllTaskNames(): ArrayList<String> = ArrayList(executor.submit(Callable { taskDatabaseDao.getAllTasksNames() }).get())
}