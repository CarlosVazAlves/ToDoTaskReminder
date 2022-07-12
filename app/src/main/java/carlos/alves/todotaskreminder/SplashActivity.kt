package carlos.alves.todotaskreminder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ToDoTaskReminderApp.instance.setupNotificationChannel()
        startApp()
    }

    private fun startApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }, 3000)
    }
}