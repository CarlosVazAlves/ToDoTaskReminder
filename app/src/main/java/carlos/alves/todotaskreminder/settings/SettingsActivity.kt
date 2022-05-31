package carlos.alves.todotaskreminder.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(SettingsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.settingsCheckLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                showMissingLocationAlertDialog()
                return@setOnClickListener
            }

            val checkLocationIntent = Intent(this, LocationDetailsActivity::class.java)
            checkLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, retrieveSelectedLocationId())
            startActivity(checkLocationIntent)
        }

        binding.settingsDeleteLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                showMissingLocationAlertDialog()
                return@setOnClickListener
            }

            viewModel.deleteLocation(selectedLocationId) //validar se o local não está a ser utilizado por uma tarefa
            loadLocations()
        }

        binding.settingsCreateNewLocationButton.setOnClickListener {
            startActivity(Intent(this, LocationEditActivity::class.java))
        }

        binding.settingsEditLocationButton.setOnClickListener {
            val selectedLocationId = retrieveSelectedLocationId()
            if (selectedLocationId == 0) {
                showMissingLocationAlertDialog()
                return@setOnClickListener
            }

            val editLocationIntent = Intent(this, LocationEditActivity::class.java)
            editLocationIntent.putExtra(LocationConstants.LOCATION_ID.description, selectedLocationId)
            startActivity(editLocationIntent)
        }

        binding.settingsBackButton.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadLocations()
    }

    private fun showMissingLocationAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.no_location_selected)
            .show()
    }

    private fun loadLocations() {
        val locationsNames = viewModel.getLocationsNames()
        val locationsAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationsNames)
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.settingsLocationsSpinner.adapter = locationsAdapter
    }

    private fun retrieveSelectedLocationId(): Int {
        val selectedLocation = binding.settingsLocationsSpinner.selectedItem as String? ?: return 0
        return viewModel.getLocationId(selectedLocation)
    }
}