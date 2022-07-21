package com.free.ra_project

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.free.ra_project.databinding.ActivityMainBinding
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface, LocationInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f
    var currentPos : DoubleArray? = null
    var savedPos : DoubleArray? = null
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
        currentPos = doubleArrayOf(_latitude, _longitude, _altitude)
    }

    override fun locationValueRequested(_latitude: Double, _longitude: Double, _altitude: Double) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _latitude.toString(), _longitude.toString())
    }
    override fun gyroValueUpdate(_degree : Float) {
        x = _degree
        //mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.angleDebug, x.toString())
        arrow.rotate(_degree)
//        if (currentPos != null && savedPos != null){
//            var direction = atan2(savedPos!![1] - currentPos!![1], savedPos!![0] - currentPos!![0])
//            var distance = sqrt((savedPos!![0] - currentPos!![0]).pow(2.0) + (savedPos!![1] - currentPos!![1]).pow(2.0))
//        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.angleDebug, distance.toString())
//        }
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