package carlos.alves.todotaskreminder.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Settings")
data class SettingsEntity(@PrimaryKey val key: String, var value: String)
