package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.TaskEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TaskRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()

    private val taskDatabaseDao = database.taskDatabaseDao()

    fun insertNewTask(task: TaskEntity) {
        executor.submit { taskDatabaseDao.insertTask(task) }
    }

    fun deleteTask(taskName: String) {
        executor.submit { taskDatabaseDao.deleteTaskByName(taskName) }
    }

    fun updateTask(task: TaskEntity): Int = executor.submit { taskDatabaseDao.updateTask(task) }.get() as Int

    fun getTask(taskName: String): TaskEntity = executor.submit(Callable { taskDatabaseDao.getTaskByName(taskName) }).get()

    fun getAllTaskNames(): List<String> = executor.submit(Callable { taskDatabaseDao.getAllTaskNames() }).get()
}