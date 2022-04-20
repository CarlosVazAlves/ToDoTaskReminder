package carlos.alves.todotaskreminder.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationEditBinding


class LocationEditActivity : AppCompatActivity() {

    private val binding: ActivityLocationEditBinding by lazy { ActivityLocationEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationEditViewModel::class.java) }
    private var isNewLocation: Boolean = true //Não pode usar lateinit em variaveis de tipo primitivo

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val newCoordinates = it.data?.getStringExtra(LocationConstants.COORDINATES.description)
            val newName = it.data?.getStringExtra(LocationConstants.NAME.description)
            val newAddress = it.data?.getStringExtra(LocationConstants.ADDRESS.description)

            binding.locationEditNameEditText.setText(newName)
            binding.locationEditAddressEditText.setText(newAddress)

            viewModel.newLocation.latLng = CustomLocation.convertStringToLatLng(newCoordinates)
            viewModel.newLocation.name = newName
            viewModel.newLocation.address = newAddress
        } //is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationId = intent.getIntExtra(LocationConstants.LOCATION_ID.description, 0)
        isNewLocation = locationId < 1

        binding.locationEditMapButton.setOnClickListener {
            val mapsActivityIntent = Intent(this, MapsActivity::class.java)
            if (!isNewLocation) {
                val coordinates = viewModel.existingLocation.coordinates
                mapsActivityIntent.putExtra(LocationConstants.COORDINATES.description, coordinates)
                mapsActivityIntent.putExtra(LocationConstants.READ_ONLY.description, false)
            }
            getContent.launch(mapsActivityIntent)
        }

        binding.locationEditBackButton.setOnClickListener { finish() }

        binding.locationEditEditCreateButton.text = if (isNewLocation) resources.getString(R.string.create) else resources.getString(R.string.edit)
        binding.locationEditEditCreateButton.setOnClickListener {
            if (!checkValidLocationData()) {
                return@setOnClickListener
            }

            if (isNewLocation) {
                viewModel.saveNewLocation()
            } else {
                viewModel.editLocation()
            }
            finish()
        }

        binding.locationEditGroupSwitch.setOnClickListener {
            showOrHideGroupFields()
        }

        binding.locationEditGroupRadioGroup.setOnCheckedChangeListener { _, _ ->
            showOrHideGroupFields()
        }

        binding.locationEditNameEditText.doAfterTextChanged {
            viewModel.newLocation.name = it?.toString()
        }

        binding.locationEditAddressEditText.doAfterTextChanged {
            viewModel.newLocation.address = it?.toString()
        }

        binding.locationEditNewGroupEditText.doAfterTextChanged {
            viewModel.newGroupName = it?.toString()
        }

        if (!isNewLocation) {
            setGroupLayout(locationId)
        }

        showOrHideGroupFields()
    }

    override fun onResume() {
        super.onResume()
        loadGroups()

        if (!isNewLocation && viewModel.existingLocation.group != null) {
            selectCurrentGroupToSpinner()
        }
    }

    private fun setGroupLayout(locationId: Int) {
        viewModel.populateExistingLocationFromDb(locationId)

        binding.locationEditNameEditText.setText(viewModel.newLocation.name)
        binding.locationEditAddressEditText.setText(viewModel.newLocation.address)

        if (viewModel.existingLocation.group != null) {
            binding.locationEditGroupSwitch.isChecked = true
            binding.locationEditExistingGroupRadioButton.performClick()
        } else {
            binding.locationEditGroupSwitch.isChecked = false
        }
    }

    private fun checkValidLocationData(): Boolean {
        if (viewModel.newLocation.name.isNullOrBlank()) {
            showAlertDialog(LocationConstants.NAME)
            return false
        }

        if (viewModel.locationNameExists(isNewLocation)) {
            showAlertDialog(LocationConstants.NAME_ALREADY_EXISTS)
            return false
        }

        if (viewModel.newLocation.address.isNullOrBlank()) {
            showAlertDialog(LocationConstants.ADDRESS)
            return false
        }

        if (viewModel.newLocation.latLng == null) {
            showAlertDialog(LocationConstants.COORDINATES)
            return false
        }

        if (!binding.locationEditGroupSwitch.isChecked) {
            viewModel.newGroupName = null
        } else {
            if (impossibleToSaveGroup()) {
                showAlertDialog(LocationConstants.GROUP)
                return false
            }
        }

        return true
    }

    private fun impossibleToSaveGroup(): Boolean {
        if (saveCurrentSpinnerSelectionOk()) {
            return false
        }
        if (!isNewGroupFieldEmpty()) {
            return false
        }
        return true
    }

    private fun isNewGroupFieldEmpty(): Boolean {
        val group = binding.locationEditNewGroupEditText.text?.toString()
        return group.isNullOrBlank()
    }

    private fun saveCurrentSpinnerSelectionOk(): Boolean {
        if(binding.locationEditExistingGroupLayout.isVisible) {
            val spinnerSelectedItem = binding.locationEditExistingGroupSpinner.selectedItem
            if (spinnerSelectedItem != null) {
                viewModel.newGroupName = spinnerSelectedItem.toString()
                return true
            }
        }
        return false
    }

    private fun selectCurrentGroupToSpinner() {
        val groups = viewModel.getGroups(false)
        val index = groups.indexOf(viewModel.newGroupName)
        binding.locationEditExistingGroupSpinner.setSelection(index)
    }

    private fun loadGroups() {
        val groups = viewModel.getGroups(true)
        val groupsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups)
        groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationEditExistingGroupSpinner.adapter = groupsAdapter
    }

    private fun showAlertDialog(fieldMissing: LocationConstants) {
        AlertDialog.Builder(this)
            .setTitle(R.string.data_error)
            .setMessage(getErrorMessage(fieldMissing))
            .show()
    }

    private fun getErrorMessage(fieldMissing: LocationConstants): Int {
        return when(fieldMissing) {
            LocationConstants.NAME -> R.string.name_missing
            LocationConstants.ADDRESS -> R.string.address_missing
            LocationConstants.COORDINATES -> R.string.coordinates_missing
            LocationConstants.GROUP -> R.string.group_missing
            LocationConstants.NAME_ALREADY_EXISTS -> R.string.name_already_exists
            else -> {
                R.string.unknown_error
            }
        }
    }

    private fun showOrHideGroupFields() {
        val switchButtonChecked = binding.locationEditGroupSwitch.isChecked
        binding.locationEditGroupRadioGroup.isVisible = switchButtonChecked
        binding.locationEditNewGroupLayout.isVisible = binding.locationEditNewGroupRadioButton.isChecked && switchButtonChecked
        binding.locationEditExistingGroupLayout.isVisible = binding.locationEditExistingGroupRadioButton.isChecked && switchButtonChecked
    }
}