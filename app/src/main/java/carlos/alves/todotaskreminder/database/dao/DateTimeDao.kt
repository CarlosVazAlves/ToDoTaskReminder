package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.DateTimeEntity

@Dao
interface DateTimeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertDateTime(dateTime: DateTimeEntity)

    @Update
    fun updateDateTime(dateTime: DateTimeEntity): Int

    @Query("DELETE FROM DateTime WHERE taskName = :taskName")
    fun deleteDateTimeByTaskName(taskName: String)

    @Query("SELECT * FROM DateTime WHERE taskName = :taskName")
    fun getDateTimeByTaskName(taskName: String) : DateTimeEntity
}