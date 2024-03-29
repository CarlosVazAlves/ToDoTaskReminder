package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.TaskEntity
import java.util.concurrent.Callable

class TaskRepository(database: ToDoTaskReminderDatabase): Repository() {

    private val taskDatabaseDao = database.taskDatabaseDao()

    fun insertNewTask(task: TaskEntity) { executor.submit { taskDatabaseDao.insertTask(task) } }

    fun updateTask(task: TaskEntity) { executor.submit { taskDatabaseDao.updateTask(task) } }

    fun deleteTask(taskId: Int) { executor.submit { taskDatabaseDao.deleteTaskById(taskId) } }

    fun deleteTask(taskName: String) { executor.submit { taskDatabaseDao.deleteTaskByName(taskName) } }

    fun getTask(taskId: Int): TaskEntity = executor.submit(Callable { taskDatabaseDao.getTaskById(taskId) }).get()

    fun getTask(taskName: String): TaskEntity? = executor.submit(Callable { taskDatabaseDao.getTaskByName(taskName) }).get()

    fun getTaskId(taskName: String): Int = executor.submit(Callable { taskDatabaseDao.getTaskIdByName(taskName) }).get()

    fun getAllTaskNames(): ArrayList<String> = ArrayList(executor.submit(Callable { taskDatabaseDao.getAllTasksNames() }).get())

    fun getAllRemindByDateTasks(): ArrayList<TaskEntity> = ArrayList(executor.submit(Callable { taskDatabaseDao.getAllRemindByDateTasks() }).get())

    fun getAllRemindByLocationTasks(): ArrayList<TaskEntity> = ArrayList(executor.submit(Callable { taskDatabaseDao.getAllRemindByLocationTasks() }).get())
}