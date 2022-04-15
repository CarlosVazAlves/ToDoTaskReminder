package carlos.alves.todotaskreminder.repository

import androidx.room.*
import carlos.alves.todotaskreminder.database.*
import carlos.alves.todotaskreminder.database.dao.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime


@Database(entities = [TaskEntity::class, DateTimeEntity::class, LocationEntity::class, OnLocationEntity::class, SettingsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class ToDoTaskReminderDatabase : RoomDatabase() {
    abstract fun taskDatabaseDao() : TaskDao
    abstract fun dateTimeDatabaseDao() : DateTimeDao
    abstract fun locationDatabaseDao() : LocationDao
    abstract fun onLocationDatabaseDao() : OnLocationDao
    abstract fun settingsDatabaseDao() : SettingsDao
}

class Converters {
    @TypeConverter
    fun toDate(dateJSON: String): LocalDate {
        val objectType: Type = object : TypeToken<LocalDate?>() {}.type
        return Gson().fromJson(dateJSON, objectType)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return Gson().toJson(date)
    }


    @TypeConverter
    fun toTime(timeJSON: String): LocalTime {
        val objectType: Type = object : TypeToken<LocalTime?>() {}.type
        return Gson().fromJson(timeJSON, objectType)
    }

    @TypeConverter
    fun fromTime(time: LocalTime): String {
        return Gson().toJson(time)
    }
}
