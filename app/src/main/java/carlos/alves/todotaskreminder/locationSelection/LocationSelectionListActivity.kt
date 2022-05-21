package carlos.alves.todotaskreminder.locationSelection

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.*
import carlos.alves.todotaskreminder.adapters.LocationSelectorAdapter
import carlos.alves.todotaskreminder.databinding.ActivityLocationSelectionListBinding

class LocationSelectionListActivity : AppCompatActivity() {

    private val binding: ActivityLocationSelectionListBinding by lazy { ActivityLocationSelectionListBinding.inflate(layoutInflater) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.location_selection_list_RecyclerView) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationSelectionListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.setNoGroupStringResource(resources.getString(R.string.without_group))
        viewModel.fetchLocations()

        val receivedLocationsIds = intent.getIntArrayExtra(ALREADY_CHECKED_LOCATIONS.description)
        viewModel.populateSelectedLocationsIds(receivedLocationsIds)

        val recyclerAdapter = LocationSelectorAdapter(viewModel.generateAllLocationsAdapterObjectList())
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // https://lev-sharone.medium.com/implement-android-recyclerview-list-of-checkboxes-with-select-all-option-double-tier-77acc4b4d41
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.locationSelectionListSaveAndReturnButton.setOnClickListener {
            val selectedLocations = recyclerAdapter.getSelectedLocations()

            if (selectedLocations.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_location_selected)
                    .show()
            } else {
                val returnData = Intent()
                returnData.putExtra(NEW_CHECKED_LOCATIONS.description, selectedLocations)
                setResult(RESULT_OK, returnData)
                finish()
            }
        }
    }
}