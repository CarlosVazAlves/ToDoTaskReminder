package carlos.alves.todotaskreminder.locationManagement

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp

class LocationsManagementViewModel : ViewModel() {

    private lateinit var locationsNames: ArrayList<String>
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository

    fun getLocationsNames(): List<String> {
        locationsNames = locationRepository.getLocationsNames()
        return locationsNames
    }

    fun getLocationId(locationName: String): Int {
        return locationRepository.getLocationIdByName(locationName)
    }

    fun deleteLocation(locationId: Int) {
        locationRepository.deleteLocation(locationId)
    }

    fun checkIfLocationIsInUse(locationId: Int): Boolean {
        return onLocationRepository.getOnLocationsCountByLocationId(locationId) > 0
    }
}