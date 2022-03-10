package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Location")
data class LocationEntity(@PrimaryKey var name: String, var group: String, var coordinates: String)
