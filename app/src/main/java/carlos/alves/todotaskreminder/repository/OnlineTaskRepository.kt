package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.OnlineTaskEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class OnlineTaskRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val onlineTaskDatabaseDao = database.onlineTaskDatabaseDao()

    fun insertOnlineTask(onlineTask: OnlineTaskEntity) { executor.submit { onlineTaskDatabaseDao.insertOnlineTask(onlineTask) } }

    fun updateOnlineTask(onlineTask: OnlineTaskEntity) { executor.submit { onlineTaskDatabaseDao.updateOnlineTask(onlineTask) } }

    fun deleteOnlineTaskByTaskId(taskId: Int) { executor.submit { onlineTaskDatabaseDao.deleteOnlineTaskByTaskId(taskId) } }

    fun deleteOnlineTaskByOnlineTaskId(onlineTaskId: Int) { executor.submit { onlineTaskDatabaseDao.deleteOnlineTaskByOnlineTaskId(onlineTaskId) } }

    fun getOnlineTaskByTaskId(taskId: Int): OnlineTaskEntity? = executor.submit(Callable { onlineTaskDatabaseDao.getOnlineTaskByTaskId(taskId) }).get()

    fun getOnlineTaskByOnlineTaskId(onlineTaskId: Int): OnlineTaskEntity? = executor.submit(Callable { onlineTaskDatabaseDao.getOnlineTaskByOnlineTaskId(onlineTaskId) }).get()
}