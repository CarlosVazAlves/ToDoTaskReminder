package carlos.alves.todotaskreminder.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import carlos.alves.todotaskreminder.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {

    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.settingsCheckLocationButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.settingsCreateNewLocationButton.setOnClickListener {
            TODO()
        }

        binding.settingsDeleteLocationButton.setOnClickListener {
            TODO()
        }

        binding.settingsCancelButton.setOnClickListener { finish() }
    }
}