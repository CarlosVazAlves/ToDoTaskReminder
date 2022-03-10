package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Task")
data class TaskEntity(@PrimaryKey var name: String, var description: String, var completed: Boolean, var remindByLocation: Boolean, var remindByDate: Boolean )
