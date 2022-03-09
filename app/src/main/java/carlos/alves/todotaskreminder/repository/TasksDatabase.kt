package carlos.alves.todotaskreminder.repository

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.sql.Date
import java.sql.Time

@Entity(tableName = "Task")
data class Task(@PrimaryKey var name: String, var description: String, var completed: Boolean, var remindByLocation: Boolean, var remindByDate: Boolean )

@Entity(tableName = "DateTime", foreignKeys = [ForeignKey(entity = Task::class,
    parentColumns = arrayOf("name"),
    childColumns = arrayOf("nameTask"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)]
)
data class DateTime(@PrimaryKey val nameTask: String, var date: Date, var time: Time)

@Entity(tableName = "Location")
data class Location(@PrimaryKey var name: String, var group: String, var coordinates: String)

@Entity(tableName = "OnLocation", foreignKeys = [
    ForeignKey(entity = Task::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("nameTask"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
    ForeignKey(entity = Location::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("nameLocation"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)
    ]
)
data class OnLocation(@PrimaryKey var nameTask: String, @PrimaryKey var nameLocation: String, var distance: Double)

@Entity(tableName = "Settings")
data class Settings(@PrimaryKey val key: String, var value: String)


@Dao
interface TasksDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task): Int

    @Query("DELETE FROM Task WHERE name = :name")
    fun deleteTaskByName(name: String)

    @Query("SELECT * FROM Task WHERE name = :name")
    fun getTaskByName(name: String) : Task

    @Query("SELECT name FROM Task")
    fun getAllTaskNames() : List<String>
}

@Dao
interface DateTimeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertDateTime(dateTime: DateTime)

    @Update
    fun updateDateTime(dateTime: DateTime): Int

    @Query("DELETE FROM DateTime WHERE nameTask = :nameTask")
    fun deleteDateTimeByNameTask(nameTask: String)

    @Query("SELECT * FROM DateTime WHERE nameTask = :nameTask")
    fun getTaskByName(nameTask: String) : DateTime
}

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLocation(location: Location)

    @Update
    fun updateLocation(location: Location): Int

    @Query("DELETE FROM Location WHERE name = :name")
    fun deleteLocationByNameTask(name: String)

    @Query("SELECT * FROM Location WHERE name = :name")
    fun getLocationByName(name: String) : Location

    @Query("SELECT * FROM Location WHERE `group` = :group")
    fun getLocationsByGroup(group: String) : List<Location>

    @Query("SELECT `group` FROM Location")
    fun getGroups(group: String) : List<String>
}

@Dao
interface OnLocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOnLocation(onLocation: OnLocation)

    @Update
    fun updateOnLocation(onLocation: OnLocation): Int

    @Query("DELETE FROM OnLocation WHERE nameTask = :nameTask")
    fun deleteOnLocationByNameTask(nameTask: String)

    @Query("SELECT * FROM OnLocation WHERE nameTask = :nameTask")
    fun getOnLocationsByNameTask(nameTask: String) : List<OnLocation>

    @Query("SELECT * FROM OnLocation WHERE nameTask = :nameTask AND distance <= :distance")
    fun getLocationsByNameTaskAndDistance(nameTask: String, distance: Double) : List<OnLocation>
}

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSetting(settings: Settings)

    @Update
    fun updateDateTime(settings: Settings): Int

    @Query("SELECT value FROM Settings WHERE `key` = :key")
    fun getTaskByName(key: String) : Settings
}

@Database(entities = [Task::class, DateTime::class, Location::class, OnLocation::class, Settings::class], version = 1)
@TypeConverters(Converters::class)
abstract class ToDoTaskReminderDatabase : RoomDatabase() {
    abstract fun tasksDatabaseDao() : TasksDao
    abstract fun dateTimeDatabaseDao() : DateTimeDao
    abstract fun locationDatabaseDao() : LocationDao
    abstract fun onLocationDatabaseDao() : OnLocationDao
    abstract fun settingsDatabaseDao() : SettingsDao
}

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
}