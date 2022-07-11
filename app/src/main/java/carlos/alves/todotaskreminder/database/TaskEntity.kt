package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Task", indices = [Index(value = ["name"], unique = true)])
data class TaskEntity(@PrimaryKey(autoGenerate = true) val id: Int, var name: String, var description: String, var completed: Boolean, var remindByLocation: Boolean, var remindByDate: Boolean )
