package carlos.alves.todotaskreminder.checkTasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.CHOSEN_TASK
import carlos.alves.todotaskreminder.databinding.ActivityTaskDetailsBinding
import java.time.format.DateTimeFormatter

class TaskDetailsActivity : AppCompatActivity() {

    private val locationsSeparator = ", "

    private val binding: ActivityTaskDetailsBinding by lazy { ActivityTaskDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(TaskDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsAndBackgroundColor()

        binding.taskDetailsBackButton.setOnClickListener { finish() }

        val chosenTaskName = intent.getStringExtra(CHOSEN_TASK.description)
        val chosenTask = viewModel.fetchTask(chosenTaskName!!)
        val onlineTaskId = viewModel.fetchOnlineTaskId()

        if (onlineTaskId != null) {
            binding.taskDetailsOnlineIdEditText.text = onlineTaskId.toString()
            binding.taskDetailsOnlineIdLayout.isVisible = true
        } else {
            binding.taskDetailsOnlineIdLayout.isVisible = false
        }

        binding.taskDetailsNameEditText.text = chosenTask.name
        binding.taskDetailsDescriptionEditText.text = chosenTask.description
        binding.taskDetailsStatusEditText.text = if (chosenTask.completed) resources.getString(R.string.complete) else resources.getString(R.string.incomplete)

        if (chosenTask.remindByLocation) {
            binding.taskDetailsLocationLayout.isVisible = true
            binding.taskDetailsLocationEditText.text = convertLocationsArrayToString()

            binding.taskDetailsDistanceTextView.isVisible = true
            binding.taskDetailsDistanceTextView.text = getString(R.string.distance_remind, viewModel.getDistance())
        } else {
            binding.taskDetailsLocationLayout.isVisible = false
            binding.taskDetailsDistanceTextView.isVisible = false
        }

        if (chosenTask.remindByDate) {
            val dateTimeEntity = viewModel.fetchDateTime()
            binding.taskDetailsDateLayout.isVisible = true
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            binding.taskDetailsDateEditText.setText(dateFormatter.format(dateTimeEntity.date))
            binding.taskDetailsTimeEditText.setText(dateTimeEntity.time.toString())
        } else {
            binding.taskDetailsDateLayout.isVisible = false
        }
    }

    private fun convertLocationsArrayToString(): String {
        val locations = viewModel.fetchOnLocations()
        val locationsStringBuilder = StringBuilder()
        locations.forEach {
            locationsStringBuilder.append(viewModel.fetchLocationName(it.locationId))
            locationsStringBuilder.append(locationsSeparator)
        }
        return locationsStringBuilder.removeSuffix(locationsSeparator).toString()
    }

    private fun setButtonsAndBackgroundColor() {
        binding.taskDetailsConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        binding.taskDetailsBackButton.setBackgroundColor(viewModel.fetchButtonsColor())
    }
}