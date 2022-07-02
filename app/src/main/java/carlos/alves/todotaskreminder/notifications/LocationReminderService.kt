package carlos.alves.todotaskreminder.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.widget.Toast
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertStringToLatLng
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.adapters.AdapterConstants
import carlos.alves.todotaskreminder.database.LocationEntity
import carlos.alves.todotaskreminder.editTasks.EditTaskActivity
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import com.google.android.gms.location.*

class LocationReminderService : BroadcastReceiver() {

    private val taskRepository = ToDoTaskReminderApp.instance.taskRepository
    private val onLocationRepository = ToDoTaskReminderApp.instance.onLocationRepository
    private val locationRepository = ToDoTaskReminderApp.instance.locationRepository
    private val permissions = PermissionsUtility.instance

    init {
        instance = this
    }

    companion object {
        lateinit var instance: LocationReminderService
            private set
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            sendLocationErrorNotification(context!!, geofencingEvent.errorCode)
            return
        }
        checkGeofences(context!!, geofencingEvent.triggeringGeofences)
    }

    fun addLocationToGeoFence(context: Context, geofenceId: String, locationToAdd: LocationEntity, distanceKm: Double) {
        val coordinates = convertStringToLatLng(locationToAdd.coordinates)!!
        val distanceMeters = distanceKm.toFloat() * 1000

        val newGeofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(
                coordinates.latitude,
                coordinates.longitude,
                distanceMeters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setNotificationResponsiveness(5000)
            .build()

        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(newGeofence)
        }.build()

        val locationIntent = Intent(context, LocationReminderService::class.java).let {
            PendingIntent.getBroadcast(context, locationToAdd.id, it, permissions.getPendingIntentMutabilityFlag()) //Android 12 exige flag
        }

        val geofencingClient = LocationServices.getGeofencingClient(context)

        geofencingClient.addGeofences(request, locationIntent)?.run {
            addOnSuccessListener {
                val message = context.getString(R.string.added_geolocation, locationToAdd.name)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                val message = context.getString(R.string.impossible_to_add_geolocation, locationToAdd.name)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeLocationFromGeofence(context: Context, geofenceId: String) {
        val geofencingClient = LocationServices.getGeofencingClient(context)
        geofencingClient.removeGeofences(listOf(geofenceId))?.run {
            val location = geofenceId.split(':')[1]
            addOnSuccessListener {
                val message = context.getString(R.string.removed_geolocation, location)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                val message = context.getString(R.string.impossible_to_remove_geolocation, location)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGeofences(context: Context, geofences: List<Geofence>) {
        val geofencesIds = geofences.map { it.requestId }
        geofencesIds.forEach { fireNotification(context, it) }
    }

    private fun getNotificationBuilder(context: Context): Notification.Builder {
        return Notification.Builder(context, ToDoTaskReminderApp.TASKS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.task_icon)
            .setContentTitle(context.getString(R.string.location_reminder))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(context, 0, Intent(), permissions.getPendingIntentMutabilityFlag()))
    }

    private fun fireNotification(context: Context, geofenceId: String) {
        val taskAndLocation = geofenceId.split(':')
        val taskId = taskAndLocation[0].toInt()
        val locationName = taskAndLocation[1]
        val task = taskRepository.getTask(taskId)
        val location = locationRepository.getLocationByName(locationName)
        val distance = onLocationRepository.getOnLocations(taskId).first().distance

        val taskName = task.name
        val notificationId = "$taskId${location.id}".toInt()

        val editTaskIntent = Intent(context, EditTaskActivity::class.java).let {
            it.putExtra(AdapterConstants.CHOSEN_TASK.description, taskName)
            PendingIntent.getActivity(context, notificationId, it, permissions.getPendingIntentMutabilityFlag())
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${location.coordinates}?q=${location.address}")).let {
            it.setPackage("com.google.android.apps.maps")
            PendingIntent.getActivity(context, taskId, it, permissions.getPendingIntentMutabilityFlag())
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = getNotificationBuilder(context)
        notification.addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.lets_go), mapIntent).build())
        notification.addAction(Notification.Action.Builder(Icon.createWithResource(context, R.mipmap.task_icon), context.getString(R.string.edit_task), editTaskIntent).build())

        val message = context.getString(R.string.we_are_close_to_location, distance, location.name, task.name)
        notificationManager.notify(notificationId, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }

    private fun sendLocationErrorNotification(context: Context, error: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotificationBuilder(context)
        val message = context.getString(R.string.impossible_get_current_location, error)
        notificationManager.notify(0, notification
            .setStyle(Notification.BigTextStyle().bigText(message))
            .build())
    }
}