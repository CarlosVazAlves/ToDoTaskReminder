package carlos.alves.todotaskreminder.editTasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.databinding.ActivityEditTaskBinding

class EditTaskActivity : AppCompatActivity() {

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