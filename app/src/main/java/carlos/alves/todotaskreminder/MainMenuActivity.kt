package carlos.alves.todotaskreminder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.checkTasks.CheckTasksListActivity
import carlos.alves.todotaskreminder.createTasks.CreateTaskActivity
import carlos.alves.todotaskreminder.databinding.ActivityMainMenuBinding
import carlos.alves.todotaskreminder.deleteTasks.DeleteTasksListActivity
import carlos.alves.todotaskreminder.editTasks.EditTasksListActivity
import carlos.alves.todotaskreminder.locationManagement.LocationsManagementActivity
import carlos.alves.todotaskreminder.settings.SettingsActivity

class MainMenuActivity : AppCompatActivity() {

    private val binding: ActivityMainMenuBinding by lazy { ActivityMainMenuBinding.inflate(layoutInflater) }

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
}