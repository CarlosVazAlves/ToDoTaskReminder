package carlos.alves.todotaskreminder.editTasks

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.*
import carlos.alves.todotaskreminder.databinding.ActivityEditTaskBinding
import carlos.alves.todotaskreminder.locationSelection.LocationSelectionListActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditTaskActivity : AppCompatActivity() {

    private val binding: ActivityEditTaskBinding by lazy { ActivityEditTaskBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(EditTaskViewModel::class.java) }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val newLocationsIds = it.data?.getIntArrayExtra(NEW_CHECKED_LOCATIONS.description)
            storeNewLocationsIds(newLocationsIds!!)
        } //is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val chosenTaskName = intent.getStringExtra(CHOSEN_TASK.description)
        viewModel.loadTask(chosenTaskName!!)

        binding.editTaskBackButton.setOnClickListener { finish() }

        binding.editTaskEditButton.setOnClickListener {
            if (!checkMissingDataOk()) return@setOnClickListener
            viewModel.editTask(applicationContext)
            finish()
        }

        binding.editTaskNameEditText.setText(viewModel.task.name)
        binding.editTaskDescriptionEditText.setText(viewModel.task.description)

        binding.editTaskSetCompletedCheckBox.isChecked = viewModel.task.completed
        binding.editTaskSetCompletedCheckBox.setOnCheckedChangeListener { _, isChecked -> viewModel.task.completed = isChecked }

        binding.editTaskReminderDateEditText.showSoftInputOnFocus = false
        binding.editTaskReminderTimeEditText.showSoftInputOnFocus = false

        binding.editTaskDateReminderCheckBox.isChecked = viewModel.task.remindByDate
        binding.editTaskReminderDateLayout.isVisible = viewModel.task.remindByDate
        if (viewModel.dateReminder != null) {
            binding.editTaskReminderDateEditText.setText(viewModel.dateReminder.toString())
        }
        if (viewModel.timeReminder != null) {
            binding.editTaskReminderTimeEditText.setText(viewModel.timeReminder.toString())
        }

        binding.editTaskLocationReminderCheckBox.isChecked = viewModel.task.remindByLocation
        binding.editTaskReminderLocationLayout.isVisible = viewModel.task.remindByLocation
        binding.editTaskChooseLocationsButton.isVisible = viewModel.task.remindByLocation
        binding.editTaskReminderDistanceEditTextNumberDecimal.setText(viewModel.distanceReminder.toString())

        binding.editTaskChooseLocationsButton.setOnClickListener {
            val locationsListActivityIntent = Intent(this, LocationSelectionListActivity::class.java)
            locationsListActivityIntent.putExtra(ALREADY_CHECKED_LOCATIONS.description, viewModel.locationsId.toIntArray())
            getContent.launch(locationsListActivityIntent)
        }

        binding.editTaskLocationReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editTaskReminderLocationLayout.isVisible = isChecked
            binding.editTaskChooseLocationsButton.isVisible = isChecked
            viewModel.task.remindByLocation = isChecked
        }

        binding.editTaskDateReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editTaskReminderDateLayout.isVisible = isChecked
            viewModel.task.remindByDate = isChecked
        }

        binding.editTaskNameEditText.doAfterTextChanged {
            viewModel.task.name = it?.toString()!!
        }

        binding.editTaskDescriptionEditText.doAfterTextChanged {
            viewModel.task.description = it?.toString()!!
        }

        binding.editTaskReminderDistanceEditTextNumberDecimal.doAfterTextChanged {
            val numberString = it?.toString()
            if (!numberString.isNullOrBlank()) {
                viewModel.distanceReminder = numberString.toDouble()
            }
        }

        binding.editTaskReminderDateEditText.setOnClickListener {
            getCalender()
        }

        binding.editTaskReminderDateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                getCalender()
            }
        }

        binding.editTaskReminderTimeEditText.setOnClickListener {
            getClock()
        }

        binding.editTaskReminderTimeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                getClock()
            }
        }

    }

    private fun storeNewLocationsIds(newLocationsIds: IntArray) {
        viewModel.locationsId.clear()
        for (locationId: Int in newLocationsIds) {
            viewModel.locationsId.add(locationId)
        }
    }

    private fun checkMissingDataOk(): Boolean {
        if (viewModel.task.name.isBlank()) {
            showMissingDataAlertDialog(R.string.name_missing)
            return false
        }
        if (viewModel.checkIfTaskNameAlreadyExists()) {
            showMissingDataAlertDialog(R.string.task_already_exists)
            return false
        }
        if (viewModel.task.description.isBlank()) {
            showMissingDataAlertDialog(R.string.description_missing)
            return false
        }
        if (viewModel.task.remindByDate && (viewModel.dateReminder == null || viewModel.timeReminder == null)) {
            showMissingDataAlertDialog(R.string.date_missing)
            return false
        }
        if (viewModel.task.remindByDate && viewModel.dateTimeAlreadyPassed()) {
            showMissingDataAlertDialog(R.string.date_time_passed)
            return false
        }
        if (viewModel.task.remindByLocation) {
            if (viewModel.locationsId.isEmpty()) {
                showMissingDataAlertDialog(R.string.no_location_selected)
                return false
            }
            if (viewModel.distanceReminder <= 0.1) {
                showMissingDataAlertDialog(R.string.invalid_distance)
                return false
            }
        }
        return true
    }

    private fun showMissingDataAlertDialog(messageId: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(messageId)
            .show()
    }

    private fun getCalender() {
        val calendar = Calendar.getInstance()
        val alreadySetDate = viewModel.dateReminder != null

        val currentYear = if (alreadySetDate) viewModel.dateReminder!!.year else calendar.get(Calendar.YEAR)
        val currentMonth = if (alreadySetDate) viewModel.dateReminder!!.month.value - 1 else calendar.get(Calendar.MONTH)
        val currentDay = if (alreadySetDate) viewModel.dateReminder!!.dayOfMonth else calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth) //month index começa no 0
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                viewModel.dateReminder = date
                binding.editTaskReminderDateEditText.setText(dateFormatter.format(date)) },
            currentYear, currentMonth, currentDay
        )
        datePickerDialog.show()
    }

    private fun getClock() {
        val calendar = Calendar.getInstance()
        val alreadySetTime = viewModel.timeReminder != null

        val currentHour = if (alreadySetTime) viewModel.timeReminder!!.hour else calendar.get(
            Calendar.HOUR_OF_DAY)
        val currentMinute = if (alreadySetTime) viewModel.timeReminder!!.minute else calendar.get(
            Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            viewModel.timeReminder = LocalTime.of(hour, minute)
            binding.editTaskReminderTimeEditText.setText(viewModel.timeReminder.toString())},
            currentHour, currentMinute, true
        )
        timePickerDialog.show()
    }
}