package carlos.alves.todotaskreminder.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import carlos.alves.todotaskreminder.databinding.ActivitySettingsBinding
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this).get(SettingsViewModel::class.java) }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val parameter = it.data?.getStringExtra(PARAMETER.description)
            val color = it.data?.getIntExtra(COLOR.description, Integer.MAX_VALUE)

            if (color != Integer.MAX_VALUE) {
                saveColorFromParameter(parameter!!, color!!)
                setButtonsAndBackgroundColor()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel.fetchColorsFromDb()

        setButtonsAndBackgroundColor()

        binding.settingsButtonsColorChangeButton.setOnClickListener { startColorActivity(BUTTONS_COLOR.description) }
        binding.settingsBackgroundColorChangeButton.setOnClickListener { startColorActivity(BACKGROUND_COLOR.description) }

        binding.settingsBackButton.setOnClickListener { finish() }
        binding.settingsStoreButton.setOnClickListener {
            viewModel.storeNewColorsInDb()
            finish()
        }
    }

    private fun startColorActivity(parameter: String) {
        val colorIntent = Intent(this, ColorActivity::class.java)
        colorIntent.putExtra(PARAMETER.description, parameter)
        colorIntent.putExtra(COLOR.description, getColorFromParameter(parameter))
        colorIntent.putExtra(BACKGROUND_COLOR.description, viewModel.backgroundColor)
        colorIntent.putExtra(BUTTONS_COLOR.description, viewModel.buttonsColor)
        getContent.launch(colorIntent)
    }

    private fun getColorFromParameter(parameter: String): Int {
        return when(parameter) {
            BUTTONS_COLOR.description -> viewModel.buttonsColor
            BACKGROUND_COLOR.description -> viewModel.backgroundColor
            else -> Integer.MAX_VALUE
        }
    }

    private fun saveColorFromParameter(parameter: String, color: Int) {
        when(parameter) {
            BUTTONS_COLOR.description -> { viewModel.buttonsColor = color }
            BACKGROUND_COLOR.description -> { viewModel.backgroundColor = color }
        }
    }

    private fun setButtonsAndBackgroundColor() {
        binding.settingsConstraint.setBackgroundColor(viewModel.backgroundColor)
        val buttonsColor = viewModel.buttonsColor
        binding.settingsBackButton.setBackgroundColor(buttonsColor)
        binding.settingsStoreButton.setBackgroundColor(buttonsColor)
        binding.settingsButtonsColorChangeButton.setBackgroundColor(buttonsColor)
        binding.settingsBackgroundColorChangeButton.setBackgroundColor(buttonsColor)
    }
}