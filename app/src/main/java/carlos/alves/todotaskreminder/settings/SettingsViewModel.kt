package carlos.alves.todotaskreminder.settings

import androidx.lifecycle.ViewModel
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.database.SettingsEntity
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class SettingsViewModel : ViewModel() {

    var buttonsColor: Int = -1
    var backgroundColor: Int = -1
    private val settingsRepository = ToDoTaskReminderApp.instance.settingsRepository

    fun fetchColorsFromDb() {
        buttonsColor = settingsRepository.getSetting(BUTTONS_COLOR.description).toInt()
        backgroundColor = settingsRepository.getSetting(BACKGROUND_COLOR.description).toInt()
    }

    fun storeNewColorsInDb() {
        settingsRepository.updateSetting(SettingsEntity(BUTTONS_COLOR.description, buttonsColor.toString()))
        settingsRepository.updateSetting(SettingsEntity(BACKGROUND_COLOR.description, backgroundColor.toString()))
    }
}