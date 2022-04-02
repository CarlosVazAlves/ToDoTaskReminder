package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.LocationEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LocationRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val locationDatabaseDao = database.locationDatabaseDao()

    fun insertLocation(location: LocationEntity) { executor.submit { locationDatabaseDao.insertLocation(location) } }

    fun updateLocation(location: LocationEntity): Int = executor.submit { locationDatabaseDao.updateLocation(location) }.get() as Int

    fun deleteLocation(locationId: Int) { executor.submit { locationDatabaseDao.deleteLocationById(locationId) } }

    fun deleteLocation(locationName: String) { executor.submit { locationDatabaseDao.deleteLocationByName(locationName) } }

    fun getLocationById(locationId: Int): LocationEntity = executor.submit(Callable { locationDatabaseDao.getLocationById(locationId) }).get()

    fun getLocationIdByName(locationName: String): Int = executor.submit(Callable { locationDatabaseDao.getLocationIdByName(locationName) }).get()

    fun getLocationsByGroup(group: String): List<LocationEntity> = executor.submit(Callable { locationDatabaseDao.getLocationsByGroup(group) }).get()

    fun getGroups(): List<String> = executor.submit(Callable { locationDatabaseDao.getGroups() }).get()

    fun getLocationsNames(): List<String> = executor.submit(Callable { locationDatabaseDao.getLocationsNames() }).get()
}