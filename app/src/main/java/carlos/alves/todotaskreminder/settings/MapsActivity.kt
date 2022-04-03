package carlos.alves.todotaskreminder.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import carlos.alves.todotaskreminder.BuildConfig
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
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

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val currentLocation = CustomLocation()

    private val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    private val geocoder by lazy { Geocoder(this@MapsActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isReadOnly = intent.getBooleanExtra(LocationConstants.READ_ONLY.description, false)

        binding.mapsSaveAndReturnButton.text = if (isReadOnly) resources.getString(R.string.back) else resources.getString(R.string.save_and_return)

        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

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

        /*binding.idSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val location = binding.idSearchView.query.toString()

                if (location != "") {
                    val geocoder = Geocoder(this@MapsActivity)

                    val address = geocoder.getFromLocationName(location, 1)[0]
                    val loc = LatLng(address.latitude, address.longitude)

                    googleMap.addMarker(MarkerOptions().position(loc).title(location))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10f))
                }
                return true
            }

        })*/

        /*binding.mapsCurrentLocationButton.setOnClickListener {
            getDeviceLocation()
        }*/

        binding.mapsSaveAndReturnButton.setOnClickListener {
            if (isReadOnly) finish()

            else {
                val returnData = Intent()

                val coordinates = CustomLocation.convertLatLngToString(currentLocation.latLng)
                if (coordinates != null) {
                    returnData.putExtra(LocationConstants.COORDINATES.description, coordinates)
                }

                val address = currentLocation.address
                if (address != null) {
                    returnData.putExtra(LocationConstants.ADDRESS.description, address)
                }

                /*val name = currentLocation.name
                if (name != null) {
                    returnData.putExtra(IntentExtraConstants.NAME.description, name)
                }*/

                setResult(RESULT_OK, returnData)
                finish()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMapClickListener(this)

        val receivedCoordinates = intent.getStringExtra(LocationConstants.COORDINATES.description)
        if (receivedCoordinates != null) {
            currentLocation.latLng = CustomLocation.convertStringToLatLng(receivedCoordinates)
            setCurrentPlaceFromLatLng(currentLocation.latLng!!)
        } else {
            getDeviceLocation()
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        getLocationPermission()

        // Use the builder to create a FindCurrentPlaceRequest.
        /*val request = FindCurrentPlaceRequest.newInstance(placeFields)
        val placeResult = placesClient.findCurrentPlace(request)

        placeResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val taskResult = task.result
                //setCurrentPlaceFromLocation()
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    val text = "Place not found: ${exception.statusCode}"
                }
            }
        }*/

        val locationResult = fusedLocationProviderClient.lastLocation

        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                setCurrentPlaceFromLocation(task.result)
            }
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    override fun onMapClick(currentCoordinates: LatLng) {
        setCurrentPlaceFromLatLng(currentCoordinates)
    }

    private fun setCurrentPlaceFromLocation(currentLocation: Location) {
        this.currentLocation.marker?.remove()
        this.currentLocation.latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        this.currentLocation.address = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)[0].getAddressLine(0)
        this.currentLocation.marker = googleMap.addMarker(MarkerOptions().position(this.currentLocation.latLng!!).title("You are here"))!!
        moveCameraToCurrentPlace()
    }

    private fun setCurrentPlaceFromLatLng(currentCoordinates: LatLng) {
        currentLocation.marker?.remove()
        currentLocation.latLng = currentCoordinates
        currentLocation.address = geocoder.getFromLocation(currentCoordinates.latitude, currentCoordinates.longitude, 1)[0].getAddressLine(0)
        currentLocation.marker = googleMap.addMarker(MarkerOptions().position(currentCoordinates).title(currentLocation.address))!! // ele prefere o currentCoordinates porque tem a certeza que não foi mudado
        moveCameraToCurrentPlace()
    }

    private fun setCurrentPlaceFromPlace(place: Place) {
        currentLocation.marker!!.remove()
        currentLocation.latLng = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
        currentLocation.address = place.address!!
        currentLocation.name = place.name!!
        currentLocation.marker = googleMap.addMarker(MarkerOptions().position(currentLocation.latLng!!).title(currentLocation.name))!!
        moveCameraToCurrentPlace()
    }

    private fun moveCameraToCurrentPlace() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation.latLng!!, 15F))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentCoordinates))
    }
}
