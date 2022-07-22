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
    private var gyroAngle = 0.0f
    private var compassAngle = 0.0f
    private var currentPos : Location? = null
    private var savedPos : Location? = null
    private var direction : Float? = null
    private var distance : Float? = null
    private lateinit var arrow : Arrow
    private lateinit var compass : Compass
    private lateinit var sensorManager : SensorManager

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


    override fun locationValueUpdate(_location: Location) {
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, _location.latitude.toString(), _location.longitude.toString())
        currentPos = _location
        if (savedPos != null){
            if (direction != null) {
                direction = currentPos!!.bearingTo(savedPos!!)
                if (direction!! > 360) {
                    direction = direction!! - 360
                }
                distance = if (distance == null) currentPos!!.distanceTo(savedPos!!) else (currentPos!!.distanceTo(savedPos!!) + distance!!) / 2
                arrow.colorize(distance!!.roundToInt())
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString())
            }
            else
                direction = currentPos!!.bearingTo(savedPos!!)
        }
    }

    override fun locationValueRequested(_location : Location) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _location.latitude.toString(), _location.longitude.toString())
        savedPos = _location
    }
    override fun gyroValueUpdate(_degree : Float) {
        gyroAngle = _degree
        if (direction != null){
            //compass.rotate(direction!! + 90 - x)
                arrow.rotate(direction!! - compassAngle - gyroAngle)
        }
    }

    override fun compassValueUpdate(_degree : Float) {
        compassAngle = _degree
        //mainScreenBinding.tvSavedCoordinates.text = getString(R.string.angleDebug, y.toString())
        compass.rotate(compassAngle - 180 - gyroAngle)
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