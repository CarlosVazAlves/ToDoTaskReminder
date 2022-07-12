package carlos.alves.todotaskreminder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R

class LocationSelectorAdapter(private val locationsNamesList: ArrayList<LocationAdapterObject>) : RecyclerView.Adapter<LocationSelectorAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.row_Checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val params: ViewGroup.MarginLayoutParams = holder.checkBox.layoutParams as ViewGroup.MarginLayoutParams

        val currentLocationAdapterObject = locationsNamesList[position]
        holder.checkBox.isChecked = currentLocationAdapterObject.isChecked
        holder.checkBox.text = currentLocationAdapterObject.name

        params.leftMargin = if (currentLocationAdapterObject is GroupObject) 0 else 30

        holder.checkBox.setOnClickListener {
            val wasChecked = currentLocationAdapterObject.isChecked
            currentLocationAdapterObject.isChecked = !wasChecked

            if (currentLocationAdapterObject is GroupObject) {
                val groupName = currentLocationAdapterObject.groupName
                locationsNamesList.forEachIndexed { index, locationAdapterObject ->
                    if (locationAdapterObject is LocationObject && locationAdapterObject.belongsToGroup == groupName) {
                        locationAdapterObject.isChecked = !wasChecked
                        notifyItemChanged(index)
                    }
                }
            }

            if (currentLocationAdapterObject is LocationObject) {
                val groupIndex = locationsNamesList.indexOfFirst { it.name == currentLocationAdapterObject.belongsToGroup }
                val groupName = locationsNamesList[groupIndex].name
                val wasGroupChecked = locationsNamesList[groupIndex].isChecked

                if (wasGroupChecked) {
                    locationsNamesList[groupIndex].isChecked = !wasChecked
                    notifyItemChanged(groupIndex)
                } else {
                    if (!wasChecked) {
                        val allLocationsFromGroup = locationsNamesList.filter { it is LocationObject && it.belongsToGroup == groupName }
                        val allLocationsFromGroupSelected = allLocationsFromGroup.all { it.isChecked }
                        locationsNamesList[groupIndex].isChecked = allLocationsFromGroupSelected
                    }
                }

                notifyItemChanged(groupIndex)
            }
        }
    }

    override fun getItemCount(): Int = locationsNamesList.size

    @Suppress("UNCHECKED_CAST")
    fun getSelectedLocations(): IntArray {
        val checkedLocations = locationsNamesList.filter { it is LocationObject && it.isChecked } as ArrayList<LocationObject>
        return checkedLocations.map { it.id }.toIntArray()
    }
}