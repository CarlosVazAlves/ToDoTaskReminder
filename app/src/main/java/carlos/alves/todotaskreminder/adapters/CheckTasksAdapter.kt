package carlos.alves.todotaskreminder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView


class CheckTasksAdapter(private val tasksNamesList: ArrayList<String>) : RecyclerView.Adapter<CheckTasksAdapter.ItemViewHolder>() {

    private var chosenTask: MutableLiveData<String> = MutableLiveData()

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //https://developer.android.com/guide/topics/ui/layout/recyclerview
        val task: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentTaskName = tasksNamesList[position]
        holder.task.text = currentTaskName

        holder.task.setOnClickListener {
            chosenTask.value = currentTaskName
        }
    }

    override fun getItemCount(): Int = tasksNamesList.size

    fun getChosenTask(): MutableLiveData<String> = chosenTask
}