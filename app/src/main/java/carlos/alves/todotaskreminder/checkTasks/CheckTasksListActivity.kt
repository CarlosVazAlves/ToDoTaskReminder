package carlos.alves.todotaskreminder.checkTasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setButtonsAndBackgroundColor()

        val taskNames = viewModel.fetchTaskNames()
        binding.checkTasksListNoTasksTextView.isVisible = taskNames.isEmpty()

        val recyclerAdapter = CheckTasksAdapter(taskNames)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
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