package carlos.alves.todotaskreminder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.notifications.LocationReminderService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ToDoTaskReminderApp.instance.setupNotificationChannel()
        ToDoTaskReminderApp.instance.renewDateReminders() // ao reinicar o telemóvel, os alarmes são perdidos
        LocationReminderService.setLocationReminderService(applicationContext, 15) // Não pode ser menos de 15 minutos, o Android não deixa

        /*if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), 1)
        }*/

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }, 3000)
    }
}