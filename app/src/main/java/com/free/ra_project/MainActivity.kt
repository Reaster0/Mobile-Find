package com.free.ra_project

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity(), LocationInterface {
    private lateinit var mainScreenBinding : ActivityMainBinding

    private lateinit var location : LocationSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        location = LocationSensor(this, this)
        Log.d("testLog", "OnCreate done")
    }

    override fun locationValueUpdate(_latitude: Double, _longitude: Double, _altitude: Double) {
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, _latitude.toString(), _longitude.toString())
    }

    override fun locationValueRequested(_latitude: Double, _longitude: Double, _altitude: Double) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _latitude.toString(), _longitude.toString())
    }

    override fun onResume() {
        super.onResume()
        mainScreenBinding.btnRegisterLocation.setOnClickListener {
            location.getLocation()
            //getLocation()
            Log.d("testLog", "Button clicked")
        }
        location.startLocationUpdates()
        Log.d("testLog", "OnResume done")
    }

    override fun onPause() {
        super.onPause()
        location.stopLocationUpdates()
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