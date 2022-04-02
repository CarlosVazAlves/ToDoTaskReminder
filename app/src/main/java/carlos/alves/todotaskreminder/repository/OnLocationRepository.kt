package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.OnLocationEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class OnLocationRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val onLocationDatabaseDao = database.onLocationDatabaseDao()

    fun insertOnLocation(onLocation: OnLocationEntity) { executor.submit { onLocationDatabaseDao.insertOnLocation(onLocation) } }

    fun updateOnLocation(onLocation: OnLocationEntity): Int = executor.submit { onLocationDatabaseDao.updateOnLocation(onLocation) }.get() as Int

    fun deleteOnLocation(taskId: Int) { executor.submit { onLocationDatabaseDao.deleteOnLocationByTaskId(taskId) } }

    fun getOnLocations(taskId: Int): List<OnLocationEntity> = executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskId(taskId) }).get()

    fun getOnLocations(taskId: Int, distance: Double): List<OnLocationEntity> = executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskIdAndDistance(taskId, distance) }).get()
}