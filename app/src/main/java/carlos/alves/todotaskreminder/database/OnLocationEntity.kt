package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "OnLocation",
    primaryKeys = ["taskName", "locationName"],
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("taskName"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = LocationEntity::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("locationName"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)]
)
data class OnLocationEntity(var taskName: String, var locationName: String, var distance: Double)
