package carlos.alves.todotaskreminder.locationManagement

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import carlos.alves.todotaskreminder.BuildConfig
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertLatLngToString
import carlos.alves.todotaskreminder.utilities.CoordinatesConverter.Companion.convertStringToLatLng
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.ToDoTaskReminderApp
import carlos.alves.todotaskreminder.databinding.ActivityMapsBinding
import carlos.alves.todotaskreminder.locationManagement.LocationConstants.*
import carlos.alves.todotaskreminder.settings.SettingsConstants
import carlos.alves.todotaskreminder.utilities.PermissionsUtility
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MapsActivity : AppCompatActivity(), GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private val zoom = 15F

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val currentCustomLocation = CustomLocation()
    private val permissions = PermissionsUtility.instance

    private val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private val geocoder by lazy { Geocoder(this@MapsActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isReadOnly = intent.getBooleanExtra(READ_ONLY.description, false)

        binding.mapsSaveAndReturnButton.text = if (isReadOnly) resources.getString(R.string.back) else resources.getString(R.string.save_and_return)
        binding.mapsSaveAndReturnButton.setBackgroundColor(
            ToDoTaskReminderApp.instance.settingsRepository.getSetting(
            SettingsConstants.BUTTONS_COLOR.description).toInt())

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps_map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.maps_autocomplete_Fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if (place.latLng != null) {
                    setCurrentPlaceFromPlace(place)
                }
            }

            override fun onError(status: Status) {
                if (status != Status.RESULT_CANCELED) {
                    Toast.makeText(applicationContext, status.statusMessage, Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.mapsSaveAndReturnButton.setOnClickListener {
            if (isReadOnly) {
                finish()
            } else {
                val returnData = Intent()

                val coordinates = convertLatLngToString(currentCustomLocation.latLng)
                if (coordinates != null) {
                    returnData.putExtra(COORDINATES.description, coordinates)
                }

                val address = currentCustomLocation.address
                if (address != null) {
                    returnData.putExtra(ADDRESS.description, address)
                }

                val name = currentCustomLocation.name
                if (name != null) {
                    returnData.putExtra(NAME.description, name)
                }

                setResult(RESULT_OK, returnData)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMapClickListener(this)

        val receivedCoordinates = intent.getStringExtra(COORDINATES.description)
        if (receivedCoordinates != null) {
            currentCustomLocation.latLng = convertStringToLatLng(receivedCoordinates)
            setCurrentPlaceFromLatLng(currentCustomLocation.latLng!!)
        } else {
            getDeviceLocation()
        }
    }

    private fun getDeviceLocation() {
        if (permissions.checkLocationPermissionsOk()) {
            val currentLocationTask = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            currentLocationTask.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    setCurrentPlaceFromLocation(it.result)
                }
            }
        }
    }

    override fun onMapClick(currentCoordinates: LatLng) {
        setCurrentPlaceFromLatLng(currentCoordinates)
    }

    private fun setCurrentPlaceFromLocation(currentLocation: Location) {
        currentCustomLocation.marker?.remove()
        currentCustomLocation.latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        currentCustomLocation.address = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)[0].getAddressLine(0)
        currentCustomLocation.marker = googleMap.addMarker(MarkerOptions().position(this.currentCustomLocation.latLng!!).title(this.currentCustomLocation.address))!!
        moveCameraToCurrentPlace()
    }

    private fun setCurrentPlaceFromLatLng(currentCoordinates: LatLng) {
        currentCustomLocation.marker?.remove()
        currentCustomLocation.latLng = currentCoordinates
        currentCustomLocation.address = geocoder.getFromLocation(currentCoordinates.latitude, currentCoordinates.longitude, 1)[0].getAddressLine(0)
        currentCustomLocation.marker = googleMap.addMarker(MarkerOptions().position(currentCoordinates).title(currentCustomLocation.address))!! // ele prefere o currentCoordinates porque tem a certeza que não foi mudado
        moveCameraToCurrentPlace()
    }

    private fun setCurrentPlaceFromPlace(place: Place) {
        currentCustomLocation.marker!!.remove()
        currentCustomLocation.latLng = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
        currentCustomLocation.address = place.address!!
        currentCustomLocation.name = place.name!!
        currentCustomLocation.marker = googleMap.addMarker(MarkerOptions().position(currentCustomLocation.latLng!!).title(currentCustomLocation.name))!!
        moveCameraToCurrentPlace()
    }

    private fun moveCameraToCurrentPlace() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCustomLocation.latLng!!, zoom))
    }
}
