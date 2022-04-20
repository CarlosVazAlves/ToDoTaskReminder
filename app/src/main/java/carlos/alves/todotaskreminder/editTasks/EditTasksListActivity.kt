package carlos.alves.todotaskreminder.editTasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.databinding.ActivityEditTasksListBinding

class EditTasksListActivity : AppCompatActivity() {

    private val binding: ActivityEditTasksListBinding by lazy { ActivityEditTasksListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editTasksListCancelButton.setOnClickListener { finish() }
    }
}