package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "OnlineTask", foreignKeys = [ForeignKey(entity = TaskEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("taskId"),
    onDelete = ForeignKey.RESTRICT)]
)
data class OnlineTaskEntity(@PrimaryKey val taskId: Int, var onlineTaskId: Int)