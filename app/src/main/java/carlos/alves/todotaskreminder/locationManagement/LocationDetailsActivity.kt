package carlos.alves.todotaskreminder.locationManagement

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationDetailsBinding
import carlos.alves.todotaskreminder.locationManagement.LocationConstants.*
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class LocationDetailsActivity : AppCompatActivity() {

    private val binding: ActivityLocationDetailsBinding by lazy { ActivityLocationDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationDetailsViewModel::class.java) }
    private val permissions = PermissionsUtility.instance
    private lateinit var coordinates: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setButtonsAndBackgroundColor()

        val locationId = intent.getIntExtra(LOCATION_ID.description, 0)
        val location = viewModel.fetchLocation(locationId)
        coordinates = location.coordinates

        binding.locationDetailsNameEditText.text = location.name
        binding.locationDetailsAddressEditText.text = location.address
        binding.locationDetailsGroupEditText.text = location.group ?: resources.getString(R.string.belongs_to_no_group)

        binding.locationBackButton.setOnClickListener { finish() }

        binding.locationDetailsMapButton.setOnClickListener {
            if (!permissions.checkLocationAndInternetPermissionsOk()) {
                permissions.askLocationAndInternetPermission(this)
            } else {
                checkInternetConnectionAndGo()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.missing_google_maps_permissions)
                .setOnDismissListener { finish() }
                .show()
        } else {
            checkInternetConnectionAndGo()
        }
    }

    private fun checkInternetConnectionAndGo() {
        if (this.permissions.checkInternetConnectionOk()) {
            startGoogleMaps()
        } else {
            AlertDialogBuilder.generateErrorDialog(this, R.string.need_internet_connection)
        }
    }

    private fun startGoogleMaps() {
        val mapsActivityIntent = Intent(this, MapsActivity::class.java)
        mapsActivityIntent.putExtra(COORDINATES.description, coordinates)
        mapsActivityIntent.putExtra(READ_ONLY.description, true)
        startActivity(mapsActivityIntent)
    }

    private fun setButtonsAndBackgroundColor() {
        binding.locationDetailsConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        val buttonsColor = viewModel.fetchButtonsColor()
        binding.locationBackButton.setBackgroundColor(buttonsColor)
        binding.locationDetailsMapButton.setBackgroundColor(buttonsColor)
    }
}