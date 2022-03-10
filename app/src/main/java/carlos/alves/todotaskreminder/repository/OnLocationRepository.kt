package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.OnLocationEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class OnLocationRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()

    private val onLocationDatabaseDao = database.onLocationDatabaseDao()

    fun insertOnLocation(onLocation: OnLocationEntity) {
        executor.submit { onLocationDatabaseDao.insertOnLocation(onLocation) }
    }

    fun deleteOnLocation(taskName: String) {
        executor.submit { onLocationDatabaseDao.deleteOnLocationByTaskName(taskName) }
    }

    fun updateOnLocation(onLocation: OnLocationEntity): Int = executor.submit { onLocationDatabaseDao.updateOnLocation(onLocation) }.get() as Int

    fun getOnLocations(taskName: String): List<OnLocationEntity> = executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskName(taskName) }).get()

    fun getOnLocations(taskName: String, distance: Double): List<OnLocationEntity> = executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskNameAndDistance(taskName, distance) }).get()
}