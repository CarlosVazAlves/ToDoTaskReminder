package carlos.alves.todotaskreminder.database

import androidx.room.ColumnInfo
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
data class OnLocationEntity(val taskId: Int, @ColumnInfo(index = true) val locationId: Int, var distance: Double)

/*locationId column references a foreign key but it is not part of an index. This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column.
public final class OnLocationEntity {
    ^
    C:\Users\Carlos Alves\AndroidStudioProjects\ToDoTaskReminder\app\build\tmp\kapt3\stubs\debug\carlos\alves\todotaskreminder\repository\ToDoTaskReminderDatabase.java:8: warning: Schema export directory is not provided to the annotation processor so we cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.
    public abstract class ToDoTaskReminderDatabase extends androidx.room.RoomDatabase {
        ^
        [WARN] Incremental annotation processing requested, but support is disabled because the following processors are not incremental: android.databinding.annotationprocessor.ProcessDataBinding (DYNAMIC).



        C:\Users\Carlos Alves\AndroidStudioProjects\ToDoTaskReminder\app\build\tmp\kapt3\stubs\debug\carlos\alves\todotaskreminder\repository\ToDoTaskReminderDatabase.java:8: warning: Schema export directory is not provided to the annotation processor so we cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.
public abstract class ToDoTaskReminderDatabase extends androidx.room.RoomDatabase {


gradle export schema*/
