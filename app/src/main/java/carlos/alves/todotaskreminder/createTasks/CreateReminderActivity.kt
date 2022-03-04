package carlos.alves.todotaskreminder.createTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityCreateReminderBinding

class CreateReminderActivity : Activity() {

    private val binding: ActivityCreateReminderBinding by lazy { ActivityCreateReminderBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.createReminderBackButton.setOnClickListener { finish() }

        binding.createReminderCreateButton.setOnClickListener {
            TODO()
        }
    }
}