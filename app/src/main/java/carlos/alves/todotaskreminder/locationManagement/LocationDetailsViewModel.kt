package carlos.alves.todotaskreminder.locationManagement

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.LocationEntity
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class LocationDetailsViewModel : ViewModel() {

    lateinit var location: LocationEntity
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    fun fetchButtonsColor(): Int = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
    fun fetchBackgroundColor(): Int = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()

    fun fetchLocation(id: Int): LocationEntity {
        location = locationRepository.getLocationById(id)
        return location
    }
}