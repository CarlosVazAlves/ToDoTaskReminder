package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.OnLocationEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class OnLocationRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val onLocationDatabaseDao = database.onLocationDatabaseDao()

    fun insertOnLocation(onLocation: OnLocationEntity) { executor.submit { onLocationDatabaseDao.insertOnLocation(onLocation) } }

    fun updateOnLocation(onLocation: OnLocationEntity) { executor.submit { onLocationDatabaseDao.updateOnLocation(onLocation) } }

    fun deleteOnLocationsByTaskId(taskId: Int) { executor.submit { onLocationDatabaseDao.deleteOnLocationsByTaskId(taskId) } }

    fun deleteOnLocationsByLocationId(locationId: Int) { executor.submit { onLocationDatabaseDao.deleteOnLocationsByLocationId(locationId) } }

    fun getOnLocations(taskId: Int): ArrayList<OnLocationEntity> = ArrayList(executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskId(taskId) }).get())

    fun getOnLocationsCountByLocationId(locationId: Int): Int = executor.submit(Callable { onLocationDatabaseDao.getOnLocationsCountByLocationId(locationId) }).get()

    fun getOnLocations(taskId: Int, distance: Double): ArrayList<OnLocationEntity> = ArrayList(executor.submit(Callable { onLocationDatabaseDao.getOnLocationsByTaskIdAndDistance(taskId, distance) }).get())
}