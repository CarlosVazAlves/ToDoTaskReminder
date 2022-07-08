package carlos.alves.todotaskreminder.deleteTasks

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.DeleteTasksAdapter
import carlos.alves.todotaskreminder.databinding.ActivityDeleteTasksListBinding
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder

class DeleteTasksListActivity : AppCompatActivity() {

    private val binding: ActivityDeleteTasksListBinding by lazy { ActivityDeleteTasksListBinding.inflate(layoutInflater) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.delete_tasks_list_RecyclerView) }
    private val viewModel by lazy { ViewModelProvider(this).get(DeleteTasksListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.fetchTasksNames()

        val recyclerAdapter = DeleteTasksAdapter(viewModel.generateTaskObjectList())
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // https://lev-sharone.medium.com/implement-android-recyclerview-list-of-checkboxes-with-select-all-option-double-tier-77acc4b4d41
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.deleteTasksListCancelButton.setOnClickListener { finish() }

        binding.deleteTasksListDeleteButton.setOnClickListener {
            val selectedTasks = recyclerAdapter.getChosenTasks()
            if (selectedTasks.all { !it.isChecked }) {
                AlertDialogBuilder.generateErrorDialog(this, R.string.no_task_selected)
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.confirmation)
                    .setMessage(R.string.are_you_sure_want_delete)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        val tasksChecked = ArrayList(selectedTasks.filter { it.isChecked })
                        tasksChecked.forEach { viewModel.deleteTask(it.name) }
                        recyclerAdapter.updateTasksNamesList(tasksChecked) }
                    .setNegativeButton(R.string.no, null)
                    .show()
            }
        }
    }
}