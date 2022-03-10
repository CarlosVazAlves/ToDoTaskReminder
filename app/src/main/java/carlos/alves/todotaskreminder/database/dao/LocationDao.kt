package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLocation(location: LocationEntity)

    @Update
    fun updateLocation(location: LocationEntity): Int

    @Query("DELETE FROM Location WHERE name = :taskName")
    fun deleteLocationByTaskName(taskName: String)

    @Query("SELECT * FROM Location WHERE name = :locationName")
    fun getLocationByName(locationName: String) : LocationEntity

    @Query("SELECT * FROM Location WHERE `group` = :group")
    fun getLocationsByGroup(group: String) : List<LocationEntity>

    @Query("SELECT `group` FROM Location")
    fun getGroups() : List<String>
}