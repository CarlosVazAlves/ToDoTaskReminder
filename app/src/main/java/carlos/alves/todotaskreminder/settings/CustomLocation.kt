package carlos.alves.todotaskreminder.settings

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class CustomLocation {

    companion object {
        fun convertLatLngToString(latLng: LatLng?): String? {
            if (latLng == null) return null
            return "${latLng.latitude}:${latLng.longitude}"
        }

        fun convertStringToLatLng(latLngString: String?): LatLng? {
            if (latLngString == null) return null
            val latLngParsed = latLngString.split(":")
            return LatLng(latLngParsed[0].toDouble(), latLngParsed[1].toDouble())
        }
    }

    var latLng: LatLng? = null
    var marker: Marker? = null
    var address: String? = null
    var name: String? = null
}