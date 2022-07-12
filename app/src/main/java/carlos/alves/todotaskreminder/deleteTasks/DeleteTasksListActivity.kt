package carlos.alves.todotaskreminder.deleteTasks

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setButtonsAndBackgroundColor()

        viewModel.fetchTaskNames()
        val taskObjects = viewModel.generateTaskObjectList()
        binding.deleteTasksListNoTasksTextView.isVisible = taskObjects.isEmpty()

        val recyclerAdapter = DeleteTasksAdapter(taskObjects)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.deleteTasksListCancelButton.setOnClickListener { finish() }

        binding.deleteTasksListDeleteButton.setOnClickListener {
            alertDialogDeleteConfirmation(recyclerAdapter)
        }
    }

    private fun alertDialogDeleteConfirmation(recyclerAdapter: DeleteTasksAdapter) {
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
                    tasksChecked.forEach { viewModel.deleteTask(this, it.name) }
                    recyclerAdapter.updateTasksNamesList(tasksChecked) }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.deleteTasksListConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        val buttonsColor = viewModel.fetchButtonsColor()
        binding.deleteTasksListDeleteButton.setBackgroundColor(buttonsColor)
        binding.deleteTasksListCancelButton.setBackgroundColor(buttonsColor)
    }
}