package carlos.alves.todotaskreminder.locationManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationsManagementBinding
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder

class LocationsManagementActivity : AppCompatActivity() {

    private val binding: ActivityLocationsManagementBinding by lazy { ActivityLocationsManagementBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationsManagementViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.locationsManagementCheckLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_location_selected)
                return@setOnClickListener
            }

            val checkLocationIntent = Intent(this, LocationDetailsActivity::class.java)
            checkLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, retrieveSelectedLocationId())
            startActivity(checkLocationIntent)
        }

        binding.locationsManagementDeleteLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_location_selected)
                return@setOnClickListener
            }

            if (viewModel.checkIfLocationIsInUse(selectedLocationId)) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.impossible_to_delete_location)
            } else {
                viewModel.deleteLocation(selectedLocationId)
                loadLocations()
                Toast.makeText(this, getString(R.string.location_successfully_deleted), Toast.LENGTH_SHORT).show()
            }
        }

        binding.locationsManagementCreateNewLocationButton.setOnClickListener {
            startActivity(Intent(this, LocationEditActivity::class.java))
        }

        binding.locationsManagementEditLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_location_selected)
                return@setOnClickListener
            }

            val editLocationIntent = Intent(this, LocationEditActivity::class.java)
            editLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, selectedLocationId)
            startActivity(editLocationIntent)
        }

        binding.locationsManagementBackButton.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadLocations()
    }

    private fun loadLocations() {
        val locationsNames = viewModel.getLocationsNames()
        val locationsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationsNames)
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationsManagementLocationsSpinner.adapter = locationsAdapter
    }

    private fun retrieveSelectedLocationId(): Int {
        val selectedLocation = binding.locationsManagementLocationsSpinner.selectedItem as String? ?: return 0
        return viewModel.getLocationId(selectedLocation)
    }
}