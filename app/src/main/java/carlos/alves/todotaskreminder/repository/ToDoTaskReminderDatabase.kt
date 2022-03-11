package carlos.alves.todotaskreminder.repository

import androidx.room.*
import carlos.alves.todotaskreminder.database.*
import carlos.alves.todotaskreminder.database.dao.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.sql.Date
import java.sql.Time


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
    fun toDate(dateJSON: String): Date {
        val objectType: Type = object : TypeToken<Date?>() {}.type
        return Gson().fromJson(dateJSON, objectType)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return Gson().toJson(date)
    }


    @TypeConverter
    fun toTime(timeJSON: String): Time {
        val objectType: Type = object : TypeToken<Date?>() {}.type
        return Gson().fromJson(timeJSON, objectType)
    }

    @TypeConverter
    fun fromTime(time: Time): String {
        return Gson().toJson(time)
    }
}
