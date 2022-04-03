package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.DateTimeEntity

@Dao
interface DateTimeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertDateTime(dateTime: DateTimeEntity)

    @Update
    fun updateDateTime(dateTime: DateTimeEntity)

    @Query("DELETE FROM DateTime WHERE taskId = :taskId")
    fun deleteDateTimeByTaskId(taskId: Int)

    @Query("SELECT * FROM DateTime WHERE taskId = :taskId")
    fun getDateTimeByTaskId(taskId: Int) : DateTimeEntity
}