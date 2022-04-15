package carlos.alves.todotaskreminder.checkTasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.databinding.ActivityCheckTasksListBinding

class CheckTasksListActivity : AppCompatActivity() {

    private val binding: ActivityCheckTasksListBinding by lazy { ActivityCheckTasksListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.checkTasksListBackButton.setOnClickListener { finish() }
    }
}