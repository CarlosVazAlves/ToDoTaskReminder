package carlos.alves.todotaskreminder.sharedTasks

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivitySharedTaskBinding
import java.time.format.DateTimeFormatter

class SharedTaskActivity : AppCompatActivity() {

    private val locationsSeparator = ", "

    private val binding: ActivitySharedTaskBinding by lazy { ActivitySharedTaskBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(SharedTaskViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isAdmin = intent.getBooleanExtra(SharedTaskConstants.IS_ADMIN.description, false)

        viewModel.sharedTaskInfo = intent.getParcelableExtra(SharedTaskConstants.SHARED_TASK_INFO.description)!!
        viewModel.onlineTaskId = intent.getStringExtra(SharedTaskConstants.ONLINE_TASK_ID.description)!!
        viewModel.populateFields()

        binding.sharedTaskBackButton.setOnClickListener { finish() }
        binding.sharedTaskDeleteButton.setOnClickListener {
            viewModel.deleteOnlineTask()
            Toast.makeText(this, getString(R.string.shared_task_successfully_deleted_from_cloud), Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.sharedTaskStoreButton.setOnClickListener {
            if (viewModel.checkIfTaskNameExists()) {
                showErrorAlertDialog(getString(R.string.task))
                return@setOnClickListener
            }
            if (viewModel.checkIfLocationNameExists()) {
                showErrorAlertDialog(getString(R.string.location))
                return@setOnClickListener
            }
            viewModel.storeInLocalDataBase(this)
            Toast.makeText(this, getString(R.string.shared_task_successfully_stored), Toast.LENGTH_SHORT).show()
        }

        binding.sharedTaskNameEditText.text = viewModel.name
        binding.sharedTaskDescriptionEditText.text = viewModel.description
        binding.sharedTaskStatusEditText.text = if (viewModel.completed) resources.getString(R.string.complete) else resources.getString(
            R.string.incomplete)
        binding.sharedTaskOnlineIdEditText.text = viewModel.onlineTaskId

        if (viewModel.remindByLocation) {
            binding.sharedTaskLocationLayout.isVisible = true
            binding.sharedTaskLocationEditText.text = convertLocationsArrayToString()

            binding.sharedTaskDistanceTextView.isVisible = true
            binding.sharedTaskDistanceTextView.text = getString(R.string.distance_remind, viewModel.distanceReminder)
        } else {
            binding.sharedTaskLocationLayout.isVisible = false
            binding.sharedTaskDistanceTextView.isVisible = false
        }

        if (viewModel.remindByDate) {
            binding.sharedTaskDateLayout.isVisible = true
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            binding.sharedTaskDateEditText.setText(dateFormatter.format(viewModel.dateReminder))
            binding.sharedTaskTimeEditText.setText(viewModel.timeReminder.toString())
        } else {
            binding.sharedTaskDateLayout.isVisible = false
        }

        binding.sharedTaskDeleteButton.isVisible = isAdmin
    }

    private fun convertLocationsArrayToString(): String {
        val locations = viewModel.locations
        val locationsStringBuilder = StringBuilder()
        locations.forEach {
            locationsStringBuilder.append(it.name)
            locationsStringBuilder.append(locationsSeparator)
        }
        return locationsStringBuilder.removeSuffix(locationsSeparator).toString()
    }

    private fun showErrorAlertDialog(problem: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(getString(R.string.impossible_to_store, problem))
            .show()
    }
}