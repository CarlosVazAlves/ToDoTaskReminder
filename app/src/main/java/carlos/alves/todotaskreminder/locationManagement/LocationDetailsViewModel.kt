package carlos.alves.todotaskreminder.locationManagement

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.LocationEntity

class LocationDetailsViewModel : ViewModel() {

    lateinit var location: LocationEntity
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository

    fun fetchLocation(id: Int): LocationEntity {
        location = locationRepository.getLocationById(id)
        return location
    }
}