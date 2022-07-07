package carlos.alves.todotaskreminder.locationManagement

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationsManagementBinding
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class LocationsManagementActivity : AppCompatActivity() {

    private val binding: ActivityLocationsManagementBinding by lazy { ActivityLocationsManagementBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationsManagementViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val permissions = PermissionsUtility.instance
        if (!permissions.checkLocationPermissionsOk()) {
            permissions.askLocationPermission(this)
        }

        binding.locationsManagementCheckLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                showErrorAlertDialog(false)
                return@setOnClickListener
            }

            val checkLocationIntent = Intent(this, LocationDetailsActivity::class.java)
            checkLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, retrieveSelectedLocationId())
            startActivity(checkLocationIntent)
        }

        binding.locationsManagementDeleteLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                showErrorAlertDialog(false)
                return@setOnClickListener
            }

            if (viewModel.checkIfLocationIsInUse(selectedLocationId)) {
                showErrorAlertDialog(true)
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
                showErrorAlertDialog(false)
                return@setOnClickListener
            }

            if (!permissions.checkInternetPermission()) {
                permissions.askInternetPermission(this)
            } else {
                val editLocationIntent = Intent(this, LocationEditActivity::class.java)
                editLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, selectedLocationId)
                startActivity(editLocationIntent)
            }
        }

        binding.locationsManagementBackButton.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadLocations()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.location_permissions_missing)
                .setOnDismissListener { finish() }
                .show()
        }
    }

    private fun showErrorAlertDialog(tryingToDeleteLocation: Boolean) {
        val errorMessage = if (tryingToDeleteLocation) R.string.impossible_to_delete_location else R.string.no_location_selected
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(errorMessage)
            .show()
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