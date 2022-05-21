package carlos.alves.todotaskreminder.utilities

import com.google.android.gms.maps.model.LatLng

class CoordinatesConverter {

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
}