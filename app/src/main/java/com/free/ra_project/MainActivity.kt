package com.free.ra_project

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.free.ra_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface, LocationInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f
    lateinit var arrow : Arrow
    lateinit var compass : Compass
    lateinit var sensorManager : SensorManager

    private lateinit var location : LocationSensor
    private lateinit var gyroSensor : GyroSensor
    private lateinit var compassSensor : CompassSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = GyroSensor(this, sensorManager)
        compassSensor = CompassSensor(this, sensorManager)

        compassSensor.startListen()
        gyroSensor.startListen()
        arrow = Arrow(mainScreenBinding.ivDirectionArrow)
        compass = Compass(mainScreenBinding.ivCompass)

        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        location = LocationSensor(this, this)
    }


    override fun locationValueUpdate(_latitude: Double, _longitude: Double, _altitude: Double) {
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, _latitude.toString(), _longitude.toString())
    }

    override fun locationValueRequested(_latitude: Double, _longitude: Double, _altitude: Double) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _latitude.toString(), _longitude.toString())
    }
    override fun gyroValueUpdate(_degree : Float) {
        x = _degree
        //mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.angleDebug, x.toString())
        arrow.rotate(_degree)
    }

    override fun compassValueUpdate(_degree : Float) {
        y = _degree
        //mainScreenBinding.tvSavedCoordinates.text = getString(R.string.angleDebug, _degree.toString())
        compass.rotate(_degree + 180 - x)
    }

    override fun onResume() {
        super.onResume()
        mainScreenBinding.btnRegisterLocation.setOnClickListener {
            location.getLocation()
            Log.d("testLog", "Button clicked")
        }
        location.startLocationUpdates()
        gyroSensor.startListen()
        compassSensor.startListen()
        Log.d("testLog", "OnResume done")
    }

    override fun onPause() {
        super.onPause()
        location.stopLocationUpdates()
        gyroSensor.stopListen()
        compassSensor.stopListen()
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