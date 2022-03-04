package carlos.alves.todotaskreminder.editTasks

import android.app.Activity
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivityEditReminderBinding

class EditReminderActivity : Activity() {

    private val binding: ActivityEditReminderBinding by lazy { ActivityEditReminderBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editReminderBackButton.setOnClickListener { finish() }

        binding.editReminderEditButton.setOnClickListener {
            TODO()
        }
    }
}