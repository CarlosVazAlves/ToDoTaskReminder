package carlos.alves.todotaskreminder.repository

import androidx.room.*
import carlos.alves.todotaskreminder.database.*
import carlos.alves.todotaskreminder.database.dao.*
import java.time.LocalDate
import java.time.LocalTime


@Database(entities = [TaskEntity::class, OnlineTaskEntity::class, DateTimeEntity::class, LocationEntity::class, OnLocationEntity::class, SettingsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class ToDoTaskReminderDatabase : RoomDatabase() {
    abstract fun taskDatabaseDao() : TaskDao
    abstract fun onlineTaskDatabaseDao() : OnlineTaskDao
    abstract fun dateTimeDatabaseDao() : DateTimeDao
    abstract fun locationDatabaseDao() : LocationDao
    abstract fun onLocationDatabaseDao() : OnLocationDao
    abstract fun settingsDatabaseDao() : SettingsDao
}

class Converters {
    @TypeConverter
    fun toDate(dateString: String): LocalDate {
        val dateElements = dateString.split("-")
        return LocalDate.of(dateElements[0].toInt(), dateElements[1].toInt(), dateElements[2].toInt())
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return date.toString()
    }


    @TypeConverter
    fun toTime(timeString: String): LocalTime {
        val timeElements = timeString.split(":")
        return LocalTime.of(timeElements[0].toInt(), timeElements[1].toInt())
    }

    @TypeConverter
    fun fromTime(time: LocalTime): String {
        return time.toString()
    }
}
