package carlos.alves.todotaskreminder.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R

class DeleteTasksAdapter(private val tasksNamesList: ArrayList<TaskObject>) : RecyclerView.Adapter<DeleteTasksAdapter.ItemViewHolder>()  {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //https://developer.android.com/guide/topics/ui/layout/recyclerview
        val checkBox: CheckBox = itemView.findViewById(R.id.row_Checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentTaskName = tasksNamesList[position]

        holder.checkBox.isChecked = currentTaskName.isChecked
        holder.checkBox.text = currentTaskName.name

        holder.checkBox.setOnClickListener {
            val checkBoxPreviousState = holder.checkBox.isChecked
            currentTaskName.isChecked = checkBoxPreviousState
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = tasksNamesList.size

    fun getChosenTasks(): ArrayList<TaskObject> = tasksNamesList

    @SuppressLint("NotifyDataSetChanged")
    fun updateTasksNamesList(deletedTasks: ArrayList<TaskObject>) {
        tasksNamesList.removeAll(deletedTasks)
        notifyDataSetChanged()
    }
}