package com.free.ra_project

import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface, LocationInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f
    var currentPos : DoubleArray? = null
    var savedPos : DoubleArray? = null
    var locme : Location? = null
    var locsaved : Location? = null
    var direction : Float? = null
    var distance : Float? = null
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


    override fun locationValueUpdate(_latitude: Double, _longitude: Double, _altitude: Double, _location: Location) {
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, _latitude.toString(), _longitude.toString())
        currentPos = doubleArrayOf(_latitude, _longitude, _altitude)
        locme = _location
        if (locme != null && locsaved != null){
            if (direction != null) {
                direction = locme!!.bearingTo(locsaved!!)
                if (direction!! > 360) {
                    direction = direction!! - 360
                }
                mainScreenBinding.tvCurrentInfo.text = getString(R.string.angleDebug, direction!!.toString() + "\n" + (direction!! - y -x).toString())
                distance = if (distance == null) locme!!.distanceTo(locsaved!!) else (locme!!.distanceTo(locsaved!!) + distance!!) / 2
                arrow.colorize(distance!!.roundToInt())
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString())
            }
            else
                direction = locme!!.bearingTo(locsaved!!)
        }
    }

    override fun locationValueRequested(_latitude: Double, _longitude: Double, _altitude: Double, _location : Location) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _latitude.toString(), _longitude.toString())
        savedPos = doubleArrayOf(_latitude, _longitude, _altitude)
        locsaved = _location
    }
    override fun gyroValueUpdate(_degree : Float) {
        x = _degree
        //mainScreenBinding.tvCurrentInfo.text = getString(R.string.angleDebug, x.toString())
        if (direction != null){
            //compass.rotate(direction!! + 90 - x)
                arrow.rotate(direction!! - y - x)
        }
    }

    override fun compassValueUpdate(_degree : Float) {
        y = _degree
        //mainScreenBinding.tvSavedCoordinates.text = getString(R.string.angleDebug, y.toString())
        compass.rotate(y - 180 - x)
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