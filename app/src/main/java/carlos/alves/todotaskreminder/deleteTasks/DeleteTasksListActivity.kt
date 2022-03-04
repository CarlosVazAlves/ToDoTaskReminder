package carlos.alves.todotaskreminder.deleteTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityDeleteTasksListBinding

class DeleteTasksListActivity : Activity() {

    private val binding: ActivityDeleteTasksListBinding by lazy { ActivityDeleteTasksListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.deleteTasksListCancelButton.setOnClickListener { finish() }

        binding.deleteTasksListDeleteButton.setOnClickListener {
            TODO()
        }
    }
}