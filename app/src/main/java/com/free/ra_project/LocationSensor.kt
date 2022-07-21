package com.free.ra_project

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

interface LocationInterface{
    fun locationValueUpdate(_latitude: Double, _longitude: Double, _altitude: Double, _location : Location)
    fun locationValueRequested(_latitude: Double, _longitude: Double, _altitude: Double, _location: Location)
}

class LocationSensor(_interLocation : LocationInterface, _activity: AppCompatActivity) {

    private var activity : AppCompatActivity? = _activity
    private var interLocation : LocationInterface = _interLocation
    private var fusedLocationClient : FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest
    private var locationCallback: LocationCallback

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationRequest = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.LOCATION_FASTEST_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    interLocation.locationValueUpdate(location.latitude, location.longitude, location.altitude, location)
                    Log.d("testLog", location.toString())
                }
            }
        }
    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_CODE)
            return
        }
        fusedLocationClient!!.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        }).addOnSuccessListener {
            location: Location? ->
                if (location != null) {
                    interLocation.locationValueRequested(location.latitude, location.longitude, location.altitude, location)
                    Log.d("testLog", "Saved OK")
                }

        }
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_CODE)
            return
        }
        fusedLocationClient!!.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
        Log.d("testLog", "LocationUpdate starts")
    }

    fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
        Log.d("testLog", "LocationUpdate stops")
    }

}