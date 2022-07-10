package carlos.alves.todotaskreminder.locationManagement

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class LocationsManagementViewModel : ViewModel() {

    private lateinit var locationsNames: ArrayList<String>
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

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