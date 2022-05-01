package carlos.alves.todotaskreminder.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import androidx.work.*
import carlos.alves.todotaskreminder.CoordinatesConverter.Companion.convertStringToLatLng
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit
import kotlin.math.*
import carlos.alves.todotaskreminder.notifications.NotificationConstants.LOCATION_CHECK

class LocationReminderService(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val earthRadius = 6371.0

    companion object {
        fun setLocationReminderService(context: Context, repeatInterval: Int) {
            val workRequest = PeriodicWorkRequestBuilder<LocationReminderService>(repeatInterval.toLong(), TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(LOCATION_CHECK.description, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
        }
    }

    private fun getLocation() {
        val notification = Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.location_reminder))

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                checkTasksWithLocationReminder(notification, it.result)
            } else {
                sendLocationErrorNotification(notification)
            }
        }
    }

    private fun sendLocationErrorNotification(notification: Notification.Builder) {
        val message = context.getString(R.string.impossible_get_current_location)
        notificationManager.notify(0, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }

    private fun checkTasksWithLocationReminder(notification: Notification.Builder, currentLocation: Location) {
        val remindByLocationTasks = taskRepository.getAllRemindByLocationTasks().filter { !it.completed }

        for (task in remindByLocationTasks) {
            val locations = onLocationRepository.getOnLocations(task.id)
            locations.forEach {
                val location = locationRepository.getLocationById(it.locationId)
                val distance = getLocationProximity(currentLocation, convertStringToLatLng(location.coordinates)!!)
                if (distance <= it.distance) {
                    val message = context.getString(R.string.we_are_close_to_location, it.distance, location.name, task.name)
                    notificationManager.notify(0, notification
                        .setStyle(Notification.BigTextStyle().bigText(message))
                        .build())
                }
            }
        }
    }

    private fun getLocationProximity(currentLocation: Location, location: LatLng): Double {
        val currentLatitude = Math.toRadians(currentLocation.latitude)
        val currentLongitude = Math.toRadians(currentLocation.longitude)

        val locationLatitude = Math.toRadians(location.latitude)
        val locationLongitude = Math.toRadians(location.longitude)

        val latitudeDifference = locationLatitude - currentLatitude
        val longitudeDifference = locationLongitude - currentLongitude

        val a = (sin(latitudeDifference / 2).pow(2.0) + (cos(currentLatitude) * cos(locationLatitude) * sin(longitudeDifference / 2).pow(2.0))) // Haversine formula
        val c = 2 * asin(sqrt(a))

        return c * earthRadius
    }

    override fun doWork(): Result {
        getLocation()
        return Result.success()
    }
}