package com.free.ra_project

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.free.ra_project.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class MainActivity : AppCompatActivity() {
    private lateinit var mainScreenBinding : ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var savedLatitude : Double = 0.0
    private var savedLongitude: Double = 0.0
    private var savedAltitude: Double = 0.0

    private var currentLatitude : Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentAltitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.LOCATION_FASTEST_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    updateUI(location)
                }
            }
        }
        Log.d("testLog", "OnCreate done")
    }

    override fun onResume() {
        super.onResume()
        mainScreenBinding.btnRegisterLocation.setOnClickListener {
            getLocation()
            Log.d("testLog", "Button clicked")
        }
        startLocationUpdates()
        Log.d("testLog", "OnResume done")
    }
    private fun updateUI(location : Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        currentAltitude = location.altitude

        val distance = FloatArray(1) {0F}
        Location.distanceBetween( currentLatitude, currentLongitude,
            savedLatitude, savedLongitude, distance)

        val tempDistance : String = distance[0].toString()
        val tempLatitude : String = currentLatitude.toString()
        val tempLongitude : String = currentLongitude.toString()

        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, tempLatitude, tempLongitude)
        mainScreenBinding.tvDistance.text = tempDistance
        Log.d("testLog", "current latitude: $currentLatitude, current longitude: $currentLongitude, " +
                "current altitude: $currentAltitude, distance to saved: $tempDistance")
    }

    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        }).addOnSuccessListener {
                location: Location? ->
            if (location != null) {
                savedLatitude = location.latitude
                savedLongitude = location.longitude
                savedAltitude = location.altitude
            }

            val tempLatitude = savedLatitude.toString()
            val tempLongitude = savedLongitude.toString()
            mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, tempLatitude, tempLongitude)
            Log.d("testLog", "saved latitude: $savedLatitude, saved longitude: $savedLongitude, saved altitude: $savedAltitude")
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
        Log.d("testLog", "LocationUpdate starts")
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("testLog", "LocationUpdate stops")
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        Log.d("testLog", "onPause done")
    }

    override fun onStop() {
        super.onStop()
        Log.d("testLog", "onStop done")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("testLog", "onDestroy done")
    }


}