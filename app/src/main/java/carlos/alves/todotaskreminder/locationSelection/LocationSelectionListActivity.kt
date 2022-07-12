package carlos.alves.todotaskreminder.locationSelection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.*
import carlos.alves.todotaskreminder.adapters.LocationSelectorAdapter
import carlos.alves.todotaskreminder.databinding.ActivityLocationSelectionListBinding
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder

class LocationSelectionListActivity : AppCompatActivity() {

    private val binding: ActivityLocationSelectionListBinding by lazy { ActivityLocationSelectionListBinding.inflate(layoutInflater) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.location_selection_list_RecyclerView) }
    private val viewModel by lazy { ViewModelProvider(this).get(LocationSelectionListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setButtonsAndBackgroundColor()

        viewModel.setNoGroupStringResource(resources.getString(R.string.without_group))
        viewModel.fetchLocations()

        val receivedLocationsIds = intent.getIntArrayExtra(ALREADY_CHECKED_LOCATIONS.description)
        viewModel.populateSelectedLocationIds(receivedLocationsIds)

        val locationAdapterObjects = viewModel.generateAllLocationsAdapterObjectList()
        binding.locationSelectionListNoTasksTextView.isVisible = locationAdapterObjects.isEmpty()

        val recyclerAdapter = LocationSelectorAdapter(locationAdapterObjects)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // https://lev-sharone.medium.com/implement-android-recyclerview-list-of-checkboxes-with-select-all-option-double-tier-77acc4b4d41
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.locationSelectionListSaveAndReturnButton.setOnClickListener {
            val selectedLocations = recyclerAdapter.getSelectedLocations()

            if (selectedLocations.isEmpty()) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_location_selected)
            } else {
                val returnData = Intent()
                returnData.putExtra(NEW_CHECKED_LOCATIONS.description, selectedLocations)
                setResult(RESULT_OK, returnData)
                finish()
            }
        }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.locationSelectionListConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        binding.locationSelectionListSaveAndReturnButton.setBackgroundColor(viewModel.fetchButtonsColor())
    }
}