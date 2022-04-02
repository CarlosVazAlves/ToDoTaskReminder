package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time


@Entity(tableName = "DateTime", foreignKeys = [ForeignKey(entity = TaskEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("taskId"),
    onDelete = ForeignKey.RESTRICT)]
)
data class DateTimeEntity(@PrimaryKey val taskId: Int, var date: Date, var time: Time)
