package carlos.alves.todotaskreminder.createTasks

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
import carlos.alves.todotaskreminder.databinding.ActivityCreateTaskBinding
import carlos.alves.todotaskreminder.locationSelection.LocationSelectionListActivity
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class CreateTaskActivity : AppCompatActivity() {

    private val binding: ActivityCreateTaskBinding by lazy { ActivityCreateTaskBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(CreateTaskViewModel::class.java) }
    private val permissions = PermissionsUtility.instance

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val newLocationsIds = it.data?.getIntArrayExtra(NEW_CHECKED_LOCATIONS.description)
            storeNewLocationsIds(newLocationsIds!!)
        } //is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.createTaskBackButton.setOnClickListener { finish() }

        binding.createTaskCreateButton.setOnClickListener {
            if (!checkMissingDataOk()) return@setOnClickListener
            viewModel.createTask(applicationContext)
            finish()
        }

        binding.createTaskReminderDateEditText.showSoftInputOnFocus = false
        binding.createTaskReminderTimeEditText.showSoftInputOnFocus = false

        binding.createTaskReminderLocationLayout.isVisible = viewModel.remindByLocation
        binding.createTaskChooseLocationsButton.isVisible = viewModel.remindByLocation

        binding.createTaskReminderDateLayout.isVisible = viewModel.remindByDate

        binding.createTaskChooseLocationsButton.setOnClickListener {
            val locationsListActivityIntent = Intent(this, LocationSelectionListActivity::class.java)
            locationsListActivityIntent.putExtra(ALREADY_CHECKED_LOCATIONS.description, viewModel.locationsId.toIntArray())
            getContent.launch(locationsListActivityIntent)
        }

        binding.createTaskLocationReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.createTaskReminderLocationLayout.isVisible = isChecked
            binding.createTaskChooseLocationsButton.isVisible = isChecked
            viewModel.remindByLocation = isChecked
        }

        binding.createTaskDateReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.createTaskReminderDateLayout.isVisible = isChecked
            viewModel.remindByDate = isChecked
        }

        binding.createTaskNameEditText.doAfterTextChanged {
            viewModel.name = it?.toString()
        }

        binding.createTaskDescriptionEditText.doAfterTextChanged {
            viewModel.description = it?.toString()
        }

        binding.createTaskReminderDistanceEditTextNumberDecimal.doAfterTextChanged {
            val numberString = it?.toString()
            if (!numberString.isNullOrBlank()) {
                viewModel.distanceReminder = numberString.toDouble()
            }
        }

        binding.createTaskReminderDateEditText.setOnClickListener {
            getCalendar()
        }

        binding.createTaskReminderDateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                getCalendar()
            }
        }

        binding.createTaskReminderTimeEditText.setOnClickListener {
            getClock()
        }

        binding.createTaskReminderTimeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                getClock()
            }
        }

    }

    private fun storeNewLocationsIds(newLocationsIds: IntArray) {
        for (locationId: Int in newLocationsIds) {
            viewModel.locationsId.add(locationId)
        }
    }

    private fun checkMissingDataOk(): Boolean {
        if (viewModel.name.isNullOrBlank()) {
            showMissingDataAlertDialog(R.string.name_missing)
            return false
        }
        if (viewModel.checkIfTaskNameAlreadyExists()) {
            showMissingDataAlertDialog(R.string.task_already_exists)
            return false
        }
        if (viewModel.description.isNullOrBlank()) {
            showMissingDataAlertDialog(R.string.description_missing)
            return false
        }
        if (viewModel.remindByDate && (viewModel.dateReminder == null || viewModel.timeReminder == null)) {
            showMissingDataAlertDialog(R.string.date_missing)
            return false
        }
        if (viewModel.remindByDate && !permissions.checkScheduleExactAlarmPermissionOk()) {
            showMissingDataAlertDialog(R.string.alarm_permission_missing)
            return false
        }
        if (viewModel.remindByDate && viewModel.dateTimeAlreadyPassed()) {
            showMissingDataAlertDialog(R.string.date_time_passed)
            return false
        }
        if (viewModel.remindByLocation) {
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

    private fun getCalendar() {
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
                binding.createTaskReminderDateEditText.setText(dateFormatter.format(date)) },
            currentYear, currentMonth, currentDay
        )
        datePickerDialog.show()
    }

    private fun getClock() {
        val calendar = Calendar.getInstance()
        val alreadySetTime = viewModel.timeReminder != null

        val currentHour = if (alreadySetTime) viewModel.timeReminder!!.hour else calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = if (alreadySetTime) viewModel.timeReminder!!.minute else calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            viewModel.timeReminder = LocalTime.of(hour, minute)
            binding.createTaskReminderTimeEditText.setText(viewModel.timeReminder.toString())},
            currentHour, currentMinute, true
        )
        timePickerDialog.show()
    }
}