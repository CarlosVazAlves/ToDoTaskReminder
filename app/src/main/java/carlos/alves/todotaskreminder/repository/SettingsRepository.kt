package carlos.alves.todotaskreminder.repository

import carlos.alves.todotaskreminder.database.SettingsEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class SettingsRepository(database: ToDoTaskReminderDatabase) {

    private val executor = Executors.newSingleThreadExecutor()
    private val settingsDatabaseDao = database.settingsDatabaseDao()

    fun insertSetting(setting: SettingsEntity) { executor.submit { settingsDatabaseDao.insertSetting(setting) } }

    fun updateSetting(setting: SettingsEntity): Int = executor.submit { settingsDatabaseDao.updateSetting(setting) }.get() as Int

    fun getSetting(key: String): String = executor.submit(Callable { settingsDatabaseDao.getSetting(key) }).get()

    fun getAllSettings(): List<SettingsEntity> = executor.submit(Callable { settingsDatabaseDao.getAllSettings() }).get()
}