package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Location", indices = [Index(value = ["name"], unique = true)])
data class LocationEntity(@PrimaryKey(autoGenerate = true) val id: Int, var name: String, var address: String, var group: String?, var coordinates: String)
