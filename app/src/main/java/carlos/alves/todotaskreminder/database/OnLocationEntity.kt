package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName = "OnLocation",
    primaryKeys = ["taskId", "locationId"],
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = LocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("locationId"),
            onDelete = ForeignKey.RESTRICT)]
)
data class OnLocationEntity(var taskId: Int, var locationId: Int, var distance: Double)
