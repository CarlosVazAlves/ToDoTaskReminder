package carlos.alves.todotaskreminder.settings

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp

class SettingsViewModel : ViewModel() {

    private lateinit var locations: ArrayList<String>
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository

    fun getLocations(): List<String> {
        locations = locationRepository.getLocationsNames()
        return locations
    }

    fun getLocationId(locationName: String): Int {
        return locationRepository.getLocationIdByName(locationName)
    }

    fun deleteLocation(locationId: Int) {
        locationRepository.deleteLocation(locationId)
    }

}