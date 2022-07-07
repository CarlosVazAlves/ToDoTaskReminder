package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.OnlineTaskEntity

@Dao
interface OnlineTaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertOnlineTask(onlineTask: OnlineTaskEntity)

    @Update
    fun updateOnlineTask(onlineTask: OnlineTaskEntity)

    @Query("DELETE FROM OnlineTask WHERE taskId = :taskId")
    fun deleteOnlineTaskByTaskId(taskId: Int)

    @Query("DELETE FROM OnlineTask WHERE onlineTaskId = :onlineTaskId")
    fun deleteOnlineTaskByOnlineTaskId(onlineTaskId: Int)

    @Query("SELECT * FROM OnlineTask WHERE taskId = :taskId")
    fun getOnlineTaskByTaskId(taskId: Int) : OnlineTaskEntity

    @Query("SELECT * FROM OnlineTask WHERE onlineTaskId = :onlineTaskId")
    fun getOnlineTaskByOnlineTaskId(onlineTaskId: Int) : OnlineTaskEntity
}