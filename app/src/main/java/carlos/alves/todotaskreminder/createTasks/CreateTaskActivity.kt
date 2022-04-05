package carlos.alves.todotaskreminder.createTasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityCreateTaskBinding

class CreateTaskActivity : Activity() {

    private val binding: ActivityCreateTaskBinding by lazy { ActivityCreateTaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.createTaskReminderBackButton.setOnClickListener { finish() }

        binding.createTaskReminderCreateButton.setOnClickListener {
            finish()
        }
    }
}