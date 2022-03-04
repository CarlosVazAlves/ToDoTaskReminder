package carlos.alves.todotaskreminder.checkTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityCheckTasksListBinding

class CheckTasksListActivity : Activity() {

    private val binding: ActivityCheckTasksListBinding by lazy { ActivityCheckTasksListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.checkTasksListBackButton.setOnClickListener { finish() }
    }
}