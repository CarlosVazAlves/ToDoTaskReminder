package carlos.alves.todotaskreminder.locationSelection

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.GroupObject
import carlos.alves.todotaskreminder.adapters.LocationAdapterObject
import carlos.alves.todotaskreminder.adapters.LocationObject
import carlos.alves.todotaskreminder.database.LocationEntity

class LocationSelectionListViewModel : ViewModel() {

    private lateinit var noGroupStringResource: String
    private lateinit var locations: ArrayList<LocationEntity>
    private lateinit var groups: ArrayList<String?>
    private val allLocationsAdapterObjectList = arrayListOf<LocationAdapterObject>()
    private val selectedLocationsIds = mutableSetOf<Int>()

    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository

    fun setNoGroupStringResource(stringResource: String) {
        noGroupStringResource = stringResource
    }

    fun fetchLocations() {
        locations = locationRepository.getLocations()
        groups = locationRepository.getGroups()
        removeNullElementsFromGroups()
    }

    fun populateSelectedLocationsIds(selectedLocationsIds: IntArray?) {
        if (selectedLocationsIds != null) {
            for (selectedLocationId: Int in selectedLocationsIds) {
                this.selectedLocationsIds.add(selectedLocationId)
            }
        }
    }

    fun generateAllLocationsAdapterObjectList(): ArrayList<LocationAdapterObject> {
        if (!groups.isNullOrEmpty()) {
            for (group in groups) {
                addGroupAndRespectiveLocationsToList(group)
            }
        }
        if (locations.any { it.group == null }) {
            addGroupAndRespectiveLocationsToList(null)
        }
        checkSelectedLocations()
        return allLocationsAdapterObjectList
    }

    private fun checkSelectedLocations() {
        if (selectedLocationsIds.isNotEmpty()) {
            for (locationAdapterObject in allLocationsAdapterObjectList) {
                if (locationAdapterObject is LocationObject && selectedLocationsIds.contains(locationAdapterObject.id)) {
                    locationAdapterObject.isChecked = true
                }
            }
            checkGroups()
        }
    }

    private fun checkGroups() {
        val groups = allLocationsAdapterObjectList.filterIsInstance<GroupObject>()
        val locations = allLocationsAdapterObjectList.filterIsInstance<LocationObject>()

        for (group in groups) {
            val locationsBelongingToGroup = locations.filter { it.belongsToGroup == group.groupName }
            group.isChecked = locationsBelongingToGroup.all { it.isChecked }
        }
    }

    private fun addGroupAndRespectiveLocationsToList(group: String?) {
        val locationsFromGroup = locations.filter { it.group == group}
        allLocationsAdapterObjectList.add(GroupObject(group ?: noGroupStringResource, ArrayList(locationsFromGroup.map { it.name })))
        allLocationsAdapterObjectList.addAll(locationsFromGroup.map { LocationObject(it.id, it.name, group ?: noGroupStringResource) })
    }

    private fun removeNullElementsFromGroups() {
        if (groups.contains(null)) {
            groups.remove(null)
        }
    }
}