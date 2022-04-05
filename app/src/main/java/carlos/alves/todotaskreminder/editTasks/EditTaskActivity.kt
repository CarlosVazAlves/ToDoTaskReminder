package carlos.alves.todotaskreminder.editTasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityEditTaskBinding

class EditTaskActivity : Activity() {

    private val binding: ActivityEditTaskBinding by lazy { ActivityEditTaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editTaskBackButton.setOnClickListener { finish() }

        binding.editTaskEditButton.setOnClickListener {
            finish()
        }
    }
}