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
import carlos.alves.todotaskreminder.settings.SettingsConstants
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskActivity
import carlos.alves.todotaskreminder.sharedTasks.SharedTaskConstants
import carlos.alves.todotaskreminder.sharedTasks.SharedTasksServer
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class MainMenuActivity : AppCompatActivity() {

    private val binding: ActivityMainMenuBinding by lazy { ActivityMainMenuBinding.inflate(layoutInflater) }
    private val sharedTasksServer = SharedTasksServer.instance
    private val permissions = PermissionsUtility.instance
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsAndBackgroundColor()

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
            if (!permissions.checkInternetPermissionOk()) {
                permissions.askInternetPermission(this)
            } else {
                checkInternetConnectionAndGo()
            }
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

    override fun onResume() {
        super.onResume()
        setButtonsAndBackgroundColor()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.missing_internet_permission)
                .setOnDismissListener { finish() }
                .show()
        } else {
            checkInternetConnectionAndGo()
        }
    }

    private fun checkInternetConnectionAndGo() {
        if (this.permissions.checkInternetConnectionOk()) {
            showLoginDialog()
        } else {
            AlertDialogBuilder.generateErrorDialog(this, R.string.need_internet_connection)
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
                                    AlertDialogBuilder.generateErrorDialog(this, R.string.invalid_password)
                                }
                            }
                        )
                    } else {
                        AlertDialogBuilder.generateErrorDialog(this, R.string.invalid_online_task_id)
                    }
                }
            )
        }
        alertDialogBuilder.create().show()
    }

    private fun setButtonsAndBackgroundColor() {
        val backgroundColor = settingsRepository.getSetting(SettingsConstants.BACKGROUND_COLOR.description).toInt()
        val buttonsColor = settingsRepository.getSetting(SettingsConstants.BUTTONS_COLOR.description).toInt()

        binding.mainMenuConstraint.setBackgroundColor(backgroundColor)
        binding.mainMenuCheckTasksButton.setBackgroundColor(buttonsColor)
        binding.mainMenuCreateNewTaskButton.setBackgroundColor(buttonsColor)
        binding.mainMenuEditTaskButton.setBackgroundColor(buttonsColor)
        binding.mainMenuDeleteTasksButton.setBackgroundColor(buttonsColor)
        binding.mainMenuSharedTasksButton.setBackgroundColor(buttonsColor)
        binding.mainMenuManageLocationsButton.setBackgroundColor(buttonsColor)
        binding.mainMenuSettingsButton.setBackgroundColor(buttonsColor)
        binding.mainMenuExitButton.setBackgroundColor(buttonsColor)
    }
}