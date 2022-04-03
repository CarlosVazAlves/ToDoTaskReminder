package carlos.alves.todotaskreminder.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityLocationDetailsBinding

class LocationDetailsActivity : AppCompatActivity() {

    private val binding: ActivityLocationDetailsBinding by lazy { ActivityLocationDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationId = intent.getIntExtra(LocationConstants.LOCATION_ID.description, 0)
        val location = viewModel.fetchLocation(locationId)

        binding.locationDetailsNameEditText.text = location.name
        binding.locationDetailsAddressEditText.text = location.address
        binding.locationDetailsGroupEditText.text = location.group ?: resources.getString(R.string.belongs_to_no_group)

        binding.locationBackButton.setOnClickListener { finish() }

        binding.locationDetailsMapButton.setOnClickListener {
            val mapsActivityIntent = Intent(this, MapsActivity::class.java)
            mapsActivityIntent.putExtra(LocationConstants.COORDINATES.description, location.coordinates)
            mapsActivityIntent.putExtra(LocationConstants.READ_ONLY.description, true)
            startActivity(mapsActivityIntent)
        }
    }
}