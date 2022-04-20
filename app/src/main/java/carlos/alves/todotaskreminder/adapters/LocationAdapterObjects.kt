package carlos.alves.todotaskreminder.adapters

abstract class LocationAdapterObject(val name: String, var isChecked: Boolean = false)

data class LocationObject(val id: Int, val locationName: String, val belongsToGroup: String) : LocationAdapterObject(locationName)

data class GroupObject(val groupName: String, val containsLocations: ArrayList<String>) : LocationAdapterObject(groupName)

data class TaskObject(val name: String, var isChecked: Boolean = false)