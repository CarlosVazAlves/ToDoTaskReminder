package carlos.alves.todotaskreminder.repository

import android.content.Context
import android.widget.Toast
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.OnlineTaskEntity
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskInfo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.random.Random
import java.security.MessageDigest

class SharedTasksRepository {

    private val serversCollection = Firebase.firestore.collection("SharedTasksServer")
    private val onlineTaskRepository = ToDoTaskReminderApp.instance.onlineTaskRepository

    init {
        instance = this
    }

    companion object {
        lateinit var instance: SharedTasksRepository
            private set
    }

    fun storeSharedTaskOnCloud(context: Context, taskId: Int, newInfo: SharedTaskInfo) {
        newInfo.adminPassword = generateHash(newInfo.adminPassword)
        newInfo.userPassword = generateHash(newInfo.userPassword)

        val onlineTaskId = Random(Calendar.getInstance().timeInMillis).nextInt(0,9999).toString()
        serversCollection.document(onlineTaskId).get()
            .addOnCompleteListener { retrieveDocument ->
                if (retrieveDocument.isSuccessful && !retrieveDocument.result.exists()) {
                    val onlineTask = serversCollection.document(onlineTaskId)
                    onlineTask.set(newInfo)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val existingOnlineTask = onlineTaskRepository.getOnlineTaskByTaskId(taskId)
                                val onlineTaskIdInt = onlineTaskId.toInt()
                                if (existingOnlineTask == null) {
                                    onlineTaskRepository.insertOnlineTask(OnlineTaskEntity(taskId, onlineTaskIdInt))
                                } else {
                                    existingOnlineTask.onlineTaskId = onlineTaskIdInt
                                    onlineTaskRepository.updateOnlineTask(existingOnlineTask)
                                }
                                Toast.makeText(context, context.getString(R.string.shared_task_successfully_sent_to_cloud, onlineTaskId), Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    storeSharedTaskOnCloud(context, taskId, newInfo)
                }
            }
    }

    fun getSharedTaskFromCloud(onlineTaskId: String, onSuccess: (SharedTaskInfo) -> Unit) {
        serversCollection.document(onlineTaskId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(task.result.toObject(SharedTaskInfo::class.java)!!)
                }
            }
    }

    fun checkIfOnlineTaskIdExists(onlineTaskId: String, onSuccess: (Boolean) -> Unit) {
        serversCollection.document(onlineTaskId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(task.result.exists())
                }
            }
    }

    fun deleteOnlineTaskFromCloud(onlineTaskId: String) {
        serversCollection.document(onlineTaskId).delete()
        onlineTaskRepository.deleteOnlineTaskByOnlineTaskId(onlineTaskId.toInt())
    }

    fun generateHash(password: String): String {
        val passwordHash = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return passwordHash.joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    }
}