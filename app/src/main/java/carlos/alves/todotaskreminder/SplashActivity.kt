package carlos.alves.todotaskreminder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import carlos.alves.todotaskreminder.utilities.PermissionsUtility

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ToDoTaskReminderApp.instance.setupNotificationChannel()

        val permissions = PermissionsUtility.instance
        if (!permissions.checkAllPermissionsOk()) {
            permissions.askAllPermissions(this)
        } else {
            startApp()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { it == -1 }) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.missing_permissions_warning)
                .setOnDismissListener { startApp() }
                .show()
        } else {
            startApp()
        }
    }

    private fun startApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }, 3000)
    }
}