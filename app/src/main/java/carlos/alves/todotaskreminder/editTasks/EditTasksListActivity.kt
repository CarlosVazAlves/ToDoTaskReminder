package carlos.alves.todotaskreminder.editTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityEditTasksListBinding

class EditTasksListActivity : Activity() {

    private val binding: ActivityEditTasksListBinding by lazy { ActivityEditTasksListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editTasksListCancelButton.setOnClickListener { finish() }
    }
}