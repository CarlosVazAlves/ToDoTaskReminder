package carlos.alves.todotaskreminder.locationManagement

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertLatLngToString
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertStringToLatLng
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.LocationEntity
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class LocationEditViewModel : ViewModel() {

    lateinit var existingLocation: LocationEntity
    val newLocation = CustomLocation()
    var newGroupName: String? = null
    private lateinit var groups: ArrayList<String?>

    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun getGroups(reloadFromDb: Boolean): ArrayList<String?> {
        if (reloadFromDb) {
            groups = locationRepository.getGroups()
            removeNullElementsFromGroups()
        }
        return groups
    }

    private fun removeNullElementsFromGroups() {
        if (groups.contains(null)) {
            groups.remove(null)
        }
    }

    fun locationNameExists(isNewLocation: Boolean): Boolean {
        if (!isNewLocation && newLocation.name == existingLocation.name) {
            return false
        }
        return locationRepository.locationNameExists(newLocation.name!!)
    }

    fun populateExistingLocationFromDb(locationId: Int) {
        existingLocation = locationRepository.getLocationById(locationId)
        newLocation.name = existingLocation.name
        newLocation.address = existingLocation.address
        newLocation.latLng = convertStringToLatLng(existingLocation.coordinates)
        newGroupName = existingLocation.group

    }

    fun saveNewLocation() {
        locationRepository.insertLocation(LocationEntity(
            0,
            newLocation.name!!,
            newLocation.address!!,
            newGroupName,
            convertLatLngToString(newLocation.latLng)!!
        ))
    }

    fun editLocation() {
        existingLocation.name = newLocation.name!!
        existingLocation.address = newLocation.address!!
        existingLocation.coordinates = convertLatLngToString(newLocation.latLng)!!
        existingLocation.group = newGroupName
        locationRepository.updateLocation(existingLocation)
    }
}