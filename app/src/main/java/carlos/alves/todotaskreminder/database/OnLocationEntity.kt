package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "OnLocation", foreignKeys = [
    ForeignKey(entity = TaskEntity::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("nameTask"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
    ForeignKey(entity = LocationEntity::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("nameLocation"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)
])
data class OnLocationEntity(@PrimaryKey var nameTask: String, @PrimaryKey var nameLocation: String, var distance: Double)
