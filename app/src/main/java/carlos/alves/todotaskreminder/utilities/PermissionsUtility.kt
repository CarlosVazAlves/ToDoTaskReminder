package carlos.alves.todotaskreminder.utilities

import android.Manifest.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class PermissionsUtility(context: Context) {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: PermissionsUtility
            private set
    }

    private val applicationContext = context

    fun checkAllPermissionsOk(): Boolean {
        return checkScheduleExactAlarmPermissionOk() && checkLocationPermissionsOk() && checkInternetPermission()
    }

    fun checkScheduleExactAlarmPermissionOk(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return checkScheduleExactAlarmOk()
        }
        return true
    }

    fun checkLocationPermissionsOk(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED
    }

    fun checkInternetConnection(): Boolean {
        if (checkInternetPermission()) {
            val connectivityManager = getSystemService(applicationContext, ConnectivityManager::class.java)!!
            val currentNetwork = connectivityManager.getActiveNetwork()
            return currentNetwork != null
        }
        return false
    }

    fun askAllPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(
            permission.ACCESS_BACKGROUND_LOCATION,
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION,
            permission.INTERNET),
            1)
    }

    private fun checkInternetPermission(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, permission.INTERNET) == PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkScheduleExactAlarmOk(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, permission.SCHEDULE_EXACT_ALARM) == PERMISSION_GRANTED
    }
}