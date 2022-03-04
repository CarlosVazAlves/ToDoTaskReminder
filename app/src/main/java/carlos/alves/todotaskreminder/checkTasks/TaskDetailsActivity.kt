package carlos.alves.todotaskreminder.checkTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityTaskDetailsBinding

class TaskDetailsActivity : Activity() {

    private val binding: ActivityTaskDetailsBinding by lazy { ActivityTaskDetailsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.taskDetailsBackButton.setOnClickListener { finish() }
    }
}