package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time


@Entity(tableName = "DateTime", foreignKeys = [ForeignKey(entity = TaskEntity::class,
    parentColumns = arrayOf("name"),
    childColumns = arrayOf("taskName"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)]
)
data class DateTimeEntity(@PrimaryKey val taskName: String, var date: Date, var time: Time)
