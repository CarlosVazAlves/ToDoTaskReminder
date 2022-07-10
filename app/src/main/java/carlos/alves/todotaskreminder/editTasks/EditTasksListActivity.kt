package carlos.alves.todotaskreminder.editTasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.adapters.AdapterConstants.CHOSEN_TASK
import carlos.alves.todotaskreminder.adapters.CheckTasksAdapter
import carlos.alves.todotaskreminder.databinding.ActivityEditTasksListBinding

class EditTasksListActivity : AppCompatActivity() {

    private val binding: ActivityEditTasksListBinding by lazy { ActivityEditTasksListBinding.inflate(layoutInflater) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.edit_tasks_list_RecyclerView) }
    private val viewModel by lazy { ViewModelProvider(this).get(EditTaskListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsAndBackgroundColor()

        val recyclerAdapter = CheckTasksAdapter(viewModel.fetchTasksNames())
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // https://lev-sharone.medium.com/implement-android-recyclerview-list-of-checkboxes-with-select-all-option-double-tier-77acc4b4d41
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        recyclerAdapter.getChosenTask().observe(this) {
            val editTaskIntent = Intent(this, EditTaskActivity::class.java)
            editTaskIntent.putExtra(CHOSEN_TASK.description, it)
            startActivity(editTaskIntent)
        }

        binding.editTasksListCancelButton.setOnClickListener { finish() }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.editTasksListConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        binding.editTasksListCancelButton.setBackgroundColor(viewModel.fetchButtonsColor())
    }
}