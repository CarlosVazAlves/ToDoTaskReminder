package carlos.alves.todotaskreminder.sharedTasks

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivitySharedTaskBinding
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import java.time.format.DateTimeFormatter

class SharedTaskActivity : AppCompatActivity() {

    private val locationsSeparator = ", "

    private val binding: ActivitySharedTaskBinding by lazy { ActivitySharedTaskBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(SharedTaskViewModel::class.java) }
    private val permissions = PermissionsUtility.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsAndBackgroundColor()

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
            if (!permissions.checkBackgroundLocationPermissionsOk()) {
                permissions.askBackgroundLocationPermission(this)
            } else {
                storeTask()
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.missing_background_location_permission)
                .setOnDismissListener { finish() }
                .show()
        } else {
            storeTask()
        }
    }

    private fun storeTask() {
        if (viewModel.checkIfTaskAlreadyDownloaded()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.online_task_already_stored)
            return
        }
        if (viewModel.checkIfTaskNameExists()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.impossible_to_store_task)
            return
        }
        if (viewModel.checkIfLocationNameExists()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.impossible_to_store_location)
            return
        }
        viewModel.storeInLocalDataBase(this)
        Toast.makeText(this, getString(R.string.shared_task_successfully_stored), Toast.LENGTH_SHORT).show()
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

    private fun setButtonsAndBackgroundColor() {
        binding.sharedTaskConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        val buttonsColor = viewModel.fetchButtonsColor()
        binding.sharedTaskStoreButton.setBackgroundColor(buttonsColor)
        binding.sharedTaskBackButton.setBackgroundColor(buttonsColor)
        binding.sharedTaskDeleteButton.setBackgroundColor(buttonsColor)
    }
}