package carlos.alves.todotaskreminder.repository

import androidx.room.*
import carlos.alves.todotaskreminder.database.*
import carlos.alves.todotaskreminder.database.dao.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Database(entities = [TaskEntity::class, DateTimeEntity::class, LocationEntity::class, OnLocationEntity::class, SettingsEntity::class], version = 1)
//@TypeConverters(Converters::class)
abstract class ToDoTaskReminderDatabase : RoomDatabase() {
    abstract fun taskDatabaseDao() : TaskDao
    abstract fun dateTimeDatabaseDao() : DateTimeDao
    abstract fun locationDatabaseDao() : LocationDao
    abstract fun onLocationDatabaseDao() : OnLocationDao
    abstract fun settingsDatabaseDao() : SettingsDao
}

/*
class Converters {
    @TypeConverter
    fun toListPoints(pointJSON: String): List<Point> {
        val listType: Type = object : TypeToken<List<Point?>?>() {}.type
        return Gson().fromJson(pointJSON, listType)
    }

    @TypeConverter
    fun fromListPoints(draw: List<Point>): String {
        return Gson().toJson(draw)
    }
}*/
