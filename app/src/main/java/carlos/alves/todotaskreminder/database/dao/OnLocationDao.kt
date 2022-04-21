package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.OnLocationEntity

@Dao
interface OnLocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOnLocation(onLocation: OnLocationEntity)

    @Update
    fun updateOnLocation(onLocation: OnLocationEntity)

    @Query("DELETE FROM OnLocation WHERE taskId = :taskId")
    fun deleteOnLocationsByTaskId(taskId: Int)

    @Query("DELETE FROM OnLocation WHERE locationId = :locationId")
    fun deleteOnLocationsByLocationId(locationId: Int)

    @Query("SELECT * FROM OnLocation WHERE taskId = :taskId")
    fun getOnLocationsByTaskId(taskId: Int) : List<OnLocationEntity>

    @Query("SELECT * FROM OnLocation WHERE taskId = :taskId AND distance <= :distance")
    fun getOnLocationsByTaskIdAndDistance(taskId: Int, distance: Double) : List<OnLocationEntity>
}