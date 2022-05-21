package carlos.alves.todotaskreminder.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.work.*
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertStringToLatLng
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.AdapterConstants
import carlos.alves.todotaskreminder.database.LocationEntity
import carlos.alves.todotaskreminder.database.OnLocationEntity
import carlos.alves.todotaskreminder.database.TaskEntity
import carlos.alves.todotaskreminder.editTasks.EditTaskActivity
import carlos.alves.todotaskreminder.notifications.NotificationConstants.LOCATION_CHECK
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit
import kotlin.math.*

class LocationReminderService(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val permissions = PermissionsUtility.instance

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val earthRadius = 6371.0

    companion object {
        fun setLocationReminderService(context: Context, repeatInterval: Int) {
            val longRepeatInterval = repeatInterval.toLong()

            val workRequest = PeriodicWorkRequestBuilder<LocationReminderService>(longRepeatInterval, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .setInitialDelay(longRepeatInterval, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(LOCATION_CHECK.description, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
        }
    }

    private fun getNotificationBuilder(): Notification.Builder {
        return Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.location_reminder))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(applicationContext, 0, Intent(), 0))
    }

    private fun getLocation() {
        if (permissions.checkLocationPermissionsOk()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val currentLocationTask = fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            currentLocationTask.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    checkTasksWithLocationReminder(it.result)
                } else {
                    sendLocationErrorNotification()
                }
            }
        } else {
            sendLocationErrorNotification()
        }
    }

    private fun sendLocationErrorNotification() {
        val notification = getNotificationBuilder()
        val message = context.getString(R.string.impossible_get_current_location)
        notificationManager.notify(0, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }

    private fun checkTasksWithLocationReminder(currentLocation: Location) {
        val remindByLocationTasks = taskRepository.getAllRemindByLocationTasks().filter { !it.completed }

        for (task in remindByLocationTasks) {
            val locations = onLocationRepository.getOnLocations(task.id)

            locations.forEach { onLocation ->
                val location = locationRepository.getLocationById(onLocation.locationId)
                val distance = getLocationProximity(currentLocation, convertStringToLatLng(location.coordinates)!!)
                if (distance <= onLocation.distance) {
                    fireNotification(task, location, onLocation)
                }
            }
        }
    }

    private fun fireNotification(task: TaskEntity, location: LocationEntity, onLocation: OnLocationEntity) {
        val taskId = task.id
        val taskName = task.name

        val editTaskIntent = Intent(context, EditTaskActivity::class.java).let {
            it.putExtra(AdapterConstants.CHOSEN_TASK.description, taskName)
            PendingIntent.getActivity(context, taskId, it, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val coordinates = location.coordinates.replace(':', ',')
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${coordinates}?q=${location.address}")).let {
            it.setPackage("com.google.android.apps.maps")
            PendingIntent.getActivity(context, taskId, it, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = getNotificationBuilder()
        notification.addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.lets_go), mapIntent).build())
        notification.addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.edit_task), editTaskIntent).build())

        val message = context.getString(R.string.we_are_close_to_location, onLocation.distance, location.name, task.name)
        notificationManager.notify(taskId, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
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
        val foregroundNotification = Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.location_reminder))
            .build()
        setForegroundAsync(ForegroundInfo(-1, foregroundNotification))
        getLocation()
        return Result.success()
    }
}