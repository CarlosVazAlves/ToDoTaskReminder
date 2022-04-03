package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLocation(location: LocationEntity)

    @Update
    fun updateLocation(location: LocationEntity)

    @Query("DELETE FROM Location WHERE id = :locationId")
    fun deleteLocationById(locationId: Int)

    @Query("DELETE FROM Location WHERE name = :locationName")
    fun deleteLocationByName(locationName: String)

    @Query("SELECT * FROM Location WHERE id = :locationId")
    fun getLocationById(locationId: Int): LocationEntity

    @Query("SELECT * FROM Location WHERE name = :locationName")
    fun getLocationByName(locationName: String): LocationEntity

    @Query("SELECT * FROM Location WHERE `group` = :group")
    fun getLocationsByGroup(group: String): List<LocationEntity>

    @Query("SELECT DISTINCT `group` FROM Location")
    fun getGroups(): List<String>

    @Query("SELECT `name` FROM Location")
    fun getLocationsNames(): List<String>

    @Query("SELECT `id` FROM Location WHERE name = :locationName")
    fun getLocationIdByName(locationName: String) : Int
}