package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.LocationEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LocationRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()

    private val locationDatabaseDao = database.locationDatabaseDao()

    fun insertLocation(location: LocationEntity) {
        executor.submit { locationDatabaseDao.insertLocation(location) }
    }

    fun deleteLocation(taskName: String) {
        executor.submit { locationDatabaseDao.deleteLocationByTaskName(taskName) }
    }

    fun updateLocation(location: LocationEntity): Int = executor.submit { locationDatabaseDao.updateLocation(location) }.get() as Int

    fun getLocationByName(locationName: String): LocationEntity = executor.submit(Callable { locationDatabaseDao.getLocationByName(locationName) }).get()

    fun getLocationsByGroup(group: String): List<LocationEntity> = executor.submit(Callable { locationDatabaseDao.getLocationsByGroup(group) }).get()

    fun getGroups(): List<String> = executor.submit(Callable { locationDatabaseDao.getGroups() }).get()
}