package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime


@Entity(tableName = "DateTime", foreignKeys = [ForeignKey(entity = TaskEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("taskId"),
    onDelete = ForeignKey.RESTRICT)]
)
data class DateTimeEntity(@PrimaryKey val taskId: Int, var date: LocalDate, var time: LocalTime)
