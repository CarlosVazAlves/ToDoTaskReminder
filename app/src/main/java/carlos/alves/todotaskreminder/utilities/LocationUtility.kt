package carlos.alves.todotaskreminder.utilities

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class LocationUtility {

    companion object {
        private const val earthRadius = 6371.0
        private const val maxDistance = 0.2

        fun isWithinDistance(storedLocation: LatLng, receivedLocation: LatLng): Boolean {
            val distance = getLocationDistance(storedLocation, receivedLocation)
            return distance <= maxDistance
        }

        private fun getLocationDistance(storedLocation: LatLng, receivedLocation: LatLng): Double {
            val currentLatitude = Math.toRadians(storedLocation.latitude)
            val currentLongitude = Math.toRadians(storedLocation.longitude)

            val locationLatitude = Math.toRadians(receivedLocation.latitude)
            val locationLongitude = Math.toRadians(receivedLocation.longitude)

            val latitudeDifference = locationLatitude - currentLatitude
            val longitudeDifference = locationLongitude - currentLongitude

            val a = (sin(latitudeDifference / 2).pow(2.0) + (cos(currentLatitude) * cos(locationLatitude) *
                    sin(longitudeDifference / 2).pow(2.0))) // Haversine formula
            val c = 2 * asin(sqrt(a))

            return c * earthRadius
        }
    }
}