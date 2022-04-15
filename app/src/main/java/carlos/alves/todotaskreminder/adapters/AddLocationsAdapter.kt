package carlos.alves.todotaskreminder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R

class AddLocationsAdapter(private val locationsNamesListToShow: ArrayList<LocationAdapterObject>) : RecyclerView.Adapter<AddLocationsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //https://developer.android.com/guide/topics/ui/layout/recyclerview
        val checkBox: CheckBox = itemView.findViewById(R.id.row_Checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val params: ViewGroup.MarginLayoutParams = holder.checkBox.layoutParams as ViewGroup.MarginLayoutParams

        val currentLocationAdapterObject = locationsNamesListToShow[position]
        holder.checkBox.isChecked = currentLocationAdapterObject.isChecked
        holder.checkBox.text = currentLocationAdapterObject.name

        params.leftMargin = if (currentLocationAdapterObject is GroupObject) 0 else 30

        holder.checkBox.setOnClickListener {
            val wasChecked = currentLocationAdapterObject.isChecked
            currentLocationAdapterObject.isChecked = !wasChecked

            if (currentLocationAdapterObject is GroupObject) {
                val groupName = currentLocationAdapterObject.groupName
                locationsNamesListToShow.forEachIndexed { index, locationAdapterObject ->
                    if (locationAdapterObject is LocationObject && locationAdapterObject.belongsToGroup == groupName) {
                        locationAdapterObject.isChecked = !wasChecked
                        notifyItemChanged(index)
                    }
                }
            }
            if (currentLocationAdapterObject is LocationObject) {
                val groupIndex = locationsNamesListToShow.indexOfFirst { it.name == currentLocationAdapterObject.belongsToGroup }
                val groupName = locationsNamesListToShow[groupIndex].name
                val wasGroupChecked = locationsNamesListToShow[groupIndex].isChecked

                if (wasGroupChecked) {
                    locationsNamesListToShow[groupIndex].isChecked = !wasChecked
                    notifyItemChanged(groupIndex)
                } else {
                    if (!wasChecked) {
                        val allLocationsFromGroup = locationsNamesListToShow.filter { it is LocationObject && it.belongsToGroup == groupName }
                        val allLocationsFromGroupSelected = allLocationsFromGroup.all { it.isChecked }
                        locationsNamesListToShow[groupIndex].isChecked = allLocationsFromGroupSelected
                    }
                }
                notifyItemChanged(groupIndex)
            }
        }
    }

    override fun getItemCount(): Int = locationsNamesListToShow.size

    @Suppress("UNCHECKED_CAST")
    fun getSelectedLocations(): IntArray {
        val checkedLocations = locationsNamesListToShow.filter { it is LocationObject && it.isChecked } as ArrayList<LocationObject>
        return checkedLocations.map { it.id }.toIntArray()
    }
}