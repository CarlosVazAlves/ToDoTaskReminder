package carlos.alves.todotaskreminder.utilities

import carlos.alves.todotaskreminder.database.LocationEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class JsonConverter {

    companion object {
        fun convertTaskToJsonTask(task: TaskEntity): String {
            return Gson().toJson(task)
        }

        fun convertJsonTaskToTask(taskJson: String): TaskEntity {
            val type: Type = object : TypeToken<TaskEntity>() {}.type
            return Gson().fromJson(taskJson, type)
        }

        fun convertDateTimeToJsonDateTime(dateTime: DateTimeJson): String {
            return Gson().toJson(dateTime)
        }

        fun convertJsonDateTimeToDateTime(dateTimeJson: String): DateTimeJson {
            val type: Type = object : TypeToken<DateTimeJson>() {}.type
            return Gson().fromJson(dateTimeJson, type)
        }

        fun convertLocationToJsonLocation(location: LocationEntity): String {
            return Gson().toJson(location)
        }

        fun convertJsonLocationToLocation(locationJson: String): LocationEntity {
            val type: Type = object : TypeToken<LocationEntity>() {}.type
            return Gson().fromJson(locationJson, type)
        }

        fun convertOnLocationToJsonOnLocation(onLocation: OnLocationEntity): String {
            return Gson().toJson(onLocation)
        }

        fun convertJsonOnLocationToOnLocation(onLocationJson: String): OnLocationEntity {
            val type: Type = object : TypeToken<OnLocationEntity>() {}.type
            return Gson().fromJson(onLocationJson, type)
        }
    }
}