package carlos.alves.todotaskreminder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.checkTasks.CheckTasksListActivity
import carlos.alves.todotaskreminder.createTasks.CreateTaskActivity
import carlos.alves.todotaskreminder.databinding.ActivityMainMenuBinding
import carlos.alves.todotaskreminder.deleteTasks.DeleteTasksListActivity
import carlos.alves.todotaskreminder.editTasks.EditTasksListActivity
import carlos.alves.todotaskreminder.locationManagement.LocationsManagementActivity
import carlos.alves.todotaskreminder.settings.SettingsActivity
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskActivity
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskConstants
import carlos.alves.todotaskreminder.sharedTasks.SharedTasksServer

class MainMenuActivity : AppCompatActivity() {

    private val binding: ActivityMainMenuBinding by lazy { ActivityMainMenuBinding.inflate(layoutInflater) }
    private val sharedTasksServer = SharedTasksServer.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mainMenuCheckTasksButton.setOnClickListener {
            startActivity(Intent(this, CheckTasksListActivity::class.java))
        }

        binding.mainMenuCreateNewTaskButton.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }

        binding.mainMenuEditTaskButton.setOnClickListener {
            startActivity(Intent(this, EditTasksListActivity::class.java))
        }

        binding.mainMenuDeleteTasksButton.setOnClickListener {
            startActivity(Intent(this, DeleteTasksListActivity::class.java))
        }

        binding.mainMenuSharedTasksButton.setOnClickListener {
            showLoginDialog()
        }

        binding.mainMenuManageLocationsButton.setOnClickListener {
            startActivity(Intent(this, LocationsManagementActivity::class.java))
        }

        binding.mainMenuSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.mainMenuExitButton.setOnClickListener {
            finishAffinity()
        }
    }

    private fun showLoginDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.enter_data)

        val passwordLayout = layoutInflater.inflate(R.layout.taskid_password_layout, null)
        alertDialogBuilder.setView(passwordLayout)
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton(R.string.next) { _, _ ->
            val onlineTaskId = (passwordLayout.findViewById<View?>(R.id.taskId_password_layout_taskId_EditText) as EditText).text.toString()
            val password = (passwordLayout.findViewById<View?>(R.id.taskId_password_layout_password_EditText) as EditText).text.toString()

            if (onlineTaskId.isBlank()) {
                Toast.makeText(this, getString(R.string.no_online_task_id), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (password.isBlank()) {
                Toast.makeText(this, getString(R.string.no_password), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            sharedTasksServer.checkIfOnlineTaskIdExists(onlineTaskId,
                onSuccess = { onlineTaskIdExists ->
                    if (onlineTaskIdExists) {
                        sharedTasksServer.getSharedTaskFromCloud(onlineTaskId,
                            onSuccess = { sharedOnlineTask ->
                                val passwordHash = sharedTasksServer.generateHash(password)
                                val isAdmin = passwordHash == sharedOnlineTask.adminPassword

                                if (passwordHash == sharedOnlineTask.userPassword || isAdmin) {
                                    val sharedTaskIntent = Intent(this, SharedTaskActivity::class.java)
                                    sharedTaskIntent.putExtra(SharedTaskConstants.IS_ADMIN.description, isAdmin)
                                    sharedTaskIntent.putExtra(SharedTaskConstants.SHARED_TASK_INFO.description, sharedOnlineTask)
                                    sharedTaskIntent.putExtra(SharedTaskConstants.ONLINE_TASK_ID.description, onlineTaskId)
                                    startActivity(sharedTaskIntent)
                                } else {
                                    showErrorAlertDialog(resources.getString(R.string.invalid_password))
                                }
                            }
                        )
                    } else {
                        showErrorAlertDialog(resources.getString(R.string.invalid_online_task_id))
                    }
                })

        }
        alertDialogBuilder.create().show()
    }

    private fun showErrorAlertDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(errorMessage)
            .show()
    }
}