package carlos.alves.todotaskreminder.checkTasks

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
import carlos.alves.todotaskreminder.databinding.ActivityCheckTasksListBinding

class CheckTasksListActivity : AppCompatActivity() {

    private val binding: ActivityCheckTasksListBinding by lazy { ActivityCheckTasksListBinding.inflate(layoutInflater) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.check_tasks_list_RecyclerView) }
    private val viewModel by lazy { ViewModelProvider(this).get(CheckTasksListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsAndBackgroundColor()

        val recyclerAdapter = CheckTasksAdapter(viewModel.fetchTasksNames())
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this) // https://lev-sharone.medium.com/implement-android-recyclerview-list-of-checkboxes-with-select-all-option-double-tier-77acc4b4d41
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        recyclerAdapter.getChosenTask().observe(this) {
            val taskDetailsIntent = Intent(this, TaskDetailsActivity::class.java)
            taskDetailsIntent.putExtra(CHOSEN_TASK.description, it)
            startActivity(taskDetailsIntent)
        }

        binding.checkTasksListBackButton.setOnClickListener { finish() }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.checkTasksListConstraint.setBackgroundColor(viewModel.fetchBackgroundColor())
        binding.checkTasksListBackButton.setBackgroundColor(viewModel.fetchButtonsColor())
    }
}