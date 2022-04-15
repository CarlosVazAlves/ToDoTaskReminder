package carlos.alves.todotaskreminder.settings

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp

class SettingsViewModel : ViewModel() {

    private lateinit var locationsNames: ArrayList<String>
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository

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

}