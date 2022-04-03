package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.SettingsEntity

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSetting(settings: SettingsEntity)

    @Update
    fun updateSetting(settings: SettingsEntity)

    @Query("SELECT value FROM Settings WHERE `key` = :key")
    fun getSetting(key: String) : String

    @Query("SELECT * FROM Settings")
    fun getAllSettings() : List<SettingsEntity>
}