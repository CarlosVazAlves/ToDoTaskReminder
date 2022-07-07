package carlos.alves.todotaskreminder.locationManagement

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationDetailsBinding
import carlos.alves.todotaskreminder.locationManagement.LocationConstants.*
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class LocationDetailsActivity : AppCompatActivity() {

    private val binding: ActivityLocationDetailsBinding by lazy { ActivityLocationDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationDetailsViewModel::class.java) }
    private lateinit var coordinates: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationId = intent.getIntExtra(LOCATION_ID.description, 0)
        val location = viewModel.fetchLocation(locationId)
        coordinates = location.coordinates

        binding.locationDetailsNameEditText.text = location.name
        binding.locationDetailsAddressEditText.text = location.address
        binding.locationDetailsGroupEditText.text = location.group ?: resources.getString(R.string.belongs_to_no_group)

        binding.locationBackButton.setOnClickListener { finish() }

        val permissions = PermissionsUtility.instance

        binding.locationDetailsMapButton.setOnClickListener {
            if (!permissions.checkInternetPermission()) {
                permissions.askInternetPermission(this)
            } else {
                startGoogleMaps()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.missing_internet_permission)
                .setOnDismissListener { finish() }
                .show()
        } else {
            startGoogleMaps()
        }
    }

    private fun startGoogleMaps() {
        val mapsActivityIntent = Intent(this, MapsActivity::class.java)
        mapsActivityIntent.putExtra(COORDINATES.description, coordinates)
        mapsActivityIntent.putExtra(READ_ONLY.description, true)
        startActivity(mapsActivityIntent)
    }
}