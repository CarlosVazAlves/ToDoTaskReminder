package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.OnlineTaskEntity
import java.util.concurrent.Callable

class OnlineTaskRepository(database: ToDoTaskReminderDatabase): Repository() {

    private val onlineTaskDatabaseDao = database.onlineTaskDatabaseDao()

    fun insertOnlineTask(onlineTask: OnlineTaskEntity) { executor.submit { onlineTaskDatabaseDao.insertOnlineTask(onlineTask) } }

    fun updateOnlineTask(onlineTask: OnlineTaskEntity) { executor.submit { onlineTaskDatabaseDao.updateOnlineTask(onlineTask) } }

    fun deleteOnlineTaskByTaskId(taskId: Int) { executor.submit { onlineTaskDatabaseDao.deleteOnlineTaskByTaskId(taskId) } }

    fun deleteOnlineTaskByOnlineTaskId(onlineTaskId: Int) { executor.submit { onlineTaskDatabaseDao.deleteOnlineTaskByOnlineTaskId(onlineTaskId) } }

    fun getOnlineTaskByTaskId(taskId: Int): OnlineTaskEntity? = executor.submit(Callable { onlineTaskDatabaseDao.getOnlineTaskByTaskId(taskId) }).get()

    fun getOnlineTaskByOnlineTaskId(onlineTaskId: Int): OnlineTaskEntity? = executor.submit(Callable { onlineTaskDatabaseDao.getOnlineTaskByOnlineTaskId(onlineTaskId) }).get()
}