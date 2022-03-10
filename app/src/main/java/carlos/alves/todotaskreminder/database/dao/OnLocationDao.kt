package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.OnLocationEntity

@Dao
interface OnLocationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOnLocation(onLocation: OnLocationEntity)

    @Update
    fun updateOnLocation(onLocation: OnLocationEntity): Int

    @Query("DELETE FROM OnLocation WHERE nameTask = :taskName")
    fun deleteOnLocationByTaskName(taskName: String)

    @Query("SELECT * FROM OnLocation WHERE nameTask = :taskName")
    fun getOnLocationsByTaskName(taskName: String) : List<OnLocationEntity>

    @Query("SELECT * FROM OnLocation WHERE nameTask = :taskName AND distance <= :distance")
    fun getOnLocationsByTaskNameAndDistance(taskName: String, distance: Double) : List<OnLocationEntity>
}