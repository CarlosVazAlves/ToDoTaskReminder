package carlos.alves.todotaskreminder.editTasks

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.*
import carlos.alves.todotaskreminder.databinding.ActivityEditTaskBinding
import carlos.alves.todotaskreminder.locationSelection.LocationSelectionListActivity
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder
import carlos.alves.todotaskreminder.utilities.PermissionsConstants
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditTaskActivity : AppCompatActivity() {

    private val binding: ActivityEditTaskBinding by lazy { ActivityEditTaskBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(EditTaskViewModel::class.java) }
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setButtonsAndBackgroundColor()

        val chosenTaskName = intent.getStringExtra(CHOSEN_TASK.description)
        viewModel.loadTask(chosenTaskName!!)

        removeNotification()

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

            val permissions = PermissionsUtility.instance
            if (!permissions.checkBackgroundLocationPermissionsOk()) {
                permissions.askBackgroundLocationPermission(this)
            }
        }

        binding.editTaskDateReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editTaskReminderDateLayout.isVisible = isChecked
            viewModel.task.remindByDate = isChecked

            if (!permissions.checkScheduleExactAlarmPermissionOk()) {
                permissions.askScheduleExactAlarmPermission(this)
            }
        }

        binding.editTaskUploadToCloudCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.uploadToCloud = isChecked
            if (isChecked) {
                setupPasswordDialog()
            }
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
            getCalendar()
        }

        binding.editTaskReminderDateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                getCalendar()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val message: Int = when(requestCode) {
            PermissionsConstants.BACKGROUND_LOCATION_PERMISSION.ordinal -> R.string.missing_background_location_permission
            PermissionsConstants.SCHEDULE_EXACT_ALARM.ordinal -> R.string.missing_schedule_exact_alarm_permission
            else -> {R.string.unknown_error}
        }

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setOnDismissListener { finish() }
                .show()
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
            AlertDialogBuilder.generateErrorDialog(this, R.string.name_missing)
            return false
        }
        if (viewModel.checkIfTaskNameAlreadyExists()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.task_already_exists)
            return false
        }
        if (viewModel.task.description.isBlank()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.description_missing)
            return false
        }
        if (viewModel.task.remindByDate && (viewModel.dateReminder == null || viewModel.timeReminder == null)) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.date_missing)
            return false
        }
        if (viewModel.task.remindByDate && !permissions.checkScheduleExactAlarmPermissionOk()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.alarm_permission_missing)
            return false
        }
        if (!viewModel.isTaskCompleted() && viewModel.task.remindByDate && viewModel.dateTimeAlreadyPassed()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.date_time_passed)
            return false
        }
        if (viewModel.task.remindByLocation) {
            if (!permissions.checkBackgroundLocationPermissionsOk()) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.missing_background_location_permission)
                return false
            }
            if (viewModel.locationsId.isEmpty()) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_location_selected)
                return false
            }
            if (viewModel.distanceReminder <= 0.1) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.invalid_distance)
                return false
            }
        }
        if (binding.editTaskUploadToCloudCheckBox.isChecked && viewModel.checkIfPasswordsNotOk()) {
            AlertDialogBuilder.generateErrorDialog(this, R.string.missing_password)
            return false
        }
        return true
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

    private fun setupPasswordDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.enter_data)

        val passwordLayout = layoutInflater.inflate(R.layout.password_layout, null)
        alertDialogBuilder.setView(passwordLayout)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton(R.string.next) { _, _ ->
            viewModel.userPassword = (passwordLayout.findViewById<View?>(R.id.password_layout_user_password_EditText) as EditText).text.toString()
            viewModel.adminPassword = (passwordLayout.findViewById<View?>(R.id.password_layout_admin_password_EditText) as EditText).text.toString()
        }
        alertDialogBuilder.create().show()
    }

    private fun removeNotification() {
        val dateNotificationActive = intent.getBooleanExtra(ACTIVE_DATE_NOTIFICATION.description, false)
        if (dateNotificationActive) {
            viewModel.removeDateNotification(this)
        }

        val locationNotificationActive = intent.getBooleanExtra(ACTIVE_LOCATION_NOTIFICATION.description, false)
        if (locationNotificationActive) {
            val notificationId = intent.getIntExtra(NOTIFICATION_ID.description, 0)
            viewModel.removeLocationNotification(this, notificationId)
        }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.editTaskConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        val buttonsColor = viewModel.fetchButtonsColor()
        binding.editTaskEditButton.setBackgroundColor(buttonsColor)
        binding.editTaskBackButton.setBackgroundColor(buttonsColor)
        binding.editTaskChooseLocationsButton.setBackgroundColor(buttonsColor)
    }
}