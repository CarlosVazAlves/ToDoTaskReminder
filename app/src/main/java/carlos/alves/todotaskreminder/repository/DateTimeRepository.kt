package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.DateTimeEntity
import java.util.concurrent.Callable

class DateTimeRepository(database: ToDoTaskReminderDatabase) : Repository() {

    private val dateTimeDatabaseDao = database.dateTimeDatabaseDao()

    fun insertDateTime(dateTime: DateTimeEntity) { executor.submit { dateTimeDatabaseDao.insertDateTime(dateTime) } }

    fun updateDateTime(dateTime: DateTimeEntity) { executor.submit { dateTimeDatabaseDao.updateDateTime(dateTime) } }

    fun deleteDateTime(taskId: Int) { executor.submit { dateTimeDatabaseDao.deleteDateTimeByTaskId(taskId) } }

    fun getDateTime(taskId: Int): DateTimeEntity = executor.submit(Callable { dateTimeDatabaseDao.getDateTimeByTaskId(taskId) }).get()
}