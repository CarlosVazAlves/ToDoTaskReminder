package carlos.alves.todotaskreminder.database.dao

import androidx.room.*
import carlos.alves.todotaskreminder.database.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTask(task: TaskEntity)

    @Update
    fun updateTask(task: TaskEntity)

    @Query("DELETE FROM Task WHERE id = :id")
    fun deleteTaskById(id: Int)

    @Query("DELETE FROM Task WHERE name = :name")
    fun deleteTaskByName(name: String)

    @Query("SELECT * FROM Task WHERE id = :id")
    fun getTaskById(id: Int) : TaskEntity

    @Query("SELECT * FROM Task WHERE name = :name")
    fun getTaskByName(name: String) : TaskEntity

    @Query("SELECT name FROM Task")
    fun getAllTasksNames() : List<String>
}