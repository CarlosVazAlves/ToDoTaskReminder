package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.DateTimeEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class DateTimeRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val dateTimeDatabaseDao = database.dateTimeDatabaseDao()

    fun insertDateTime(dateTime: DateTimeEntity) { executor.submit { dateTimeDatabaseDao.insertDateTime(dateTime) } }

    fun updateDateTime(dateTime: DateTimeEntity): Int = executor.submit { dateTimeDatabaseDao.updateDateTime(dateTime) }.get() as Int

    fun deleteDateTime(taskId: Int) { executor.submit { dateTimeDatabaseDao.deleteDateTimeByTaskId(taskId) } }

    fun getDateTime(taskId: Int): DateTimeEntity = executor.submit(Callable { dateTimeDatabaseDao.getDateTimeByTaskId(taskId) }).get()
}