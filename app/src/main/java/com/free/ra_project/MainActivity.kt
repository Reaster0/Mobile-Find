package com.free.ra_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding

import kotlin.math.roundToInt
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface, LocationInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    private var gyroAngle = 0.0f
    private var compassAngle = 0.0f
    private var currentPos : Location? = null
    private var savedPos : Location? = null
    private var direction : Float? = null
    private var distance : Float = 0.0f
    private lateinit var arrow : Arrow
    private lateinit var altitudeArrow : AltitudeArrow
    private lateinit var compass : Compass
    private lateinit var sensorManager : SensorManager

    private lateinit var location : LocationSensor
    private lateinit var gyroSensor : GyroSensor
    private lateinit var compassSensor : CompassSensor
    private val database = FirebaseLocation()

    private val saveLocationActivity = 0
    private val listLocationActivity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = GyroSensor(this, sensorManager)
        compassSensor = CompassSensor(this, sensorManager, currentPos)

        compassSensor.startListen()
        gyroSensor.startListen()
        arrow = Arrow(mainScreenBinding.ivDirectionArrow)
        altitudeArrow = AltitudeArrow(mainScreenBinding.ivAltitudeArrow, mainScreenBinding.tvDiffAltitude)
        compass = Compass(mainScreenBinding.ivCompass)

        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        location = LocationSensor(this, this)
    }

    override fun locationValueUpdate(_location: Location) {
        if (currentPos == null)
            currentPos = _location
        else {
            currentPos!!.latitude = (((currentPos!!.latitude + _location.latitude) / 2 * 10000000.0).roundToInt() / 10000000.0).toDouble()
            currentPos!!.longitude = (((currentPos!!.longitude + _location.longitude) / 2 * 10000000.0).roundToInt() / 10000000.0).toDouble()
            currentPos!!.altitude = (((currentPos!!.altitude + _location.altitude) / 2 * 10000000000000.0).roundToLong() / 10000000000000.0).toDouble()
            currentPos!!.time = _location.time
        }
        if (savedPos != null){
            direction = bearingTo(currentPos!!.latitude, currentPos!!.longitude, savedPos!!.latitude, savedPos!!.longitude).toFloat()
            distance = distanceKm(currentPos!!.latitude, savedPos!!.latitude, currentPos!!.longitude, savedPos!!.longitude, currentPos!!.altitude, savedPos!!.altitude).toFloat()
            arrow.colorize(distance!!.roundToInt())
            distance = ((distance * 100.0).roundToInt() / 100.0).toFloat() // round to 2 decimal places
            val diffAltitude : Int = (savedPos!!.altitude - currentPos!!.altitude).toInt()
            altitudeArrow.rotate(diffAltitude)

            if (distance < 1.5f)
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, "<1.5m")
            else
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString() + "m")
        }
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, currentPos!!.latitude.toString(), currentPos!!.longitude.toString())
    }

    override fun locationValueRequested(_location : Location, _name : String) {
        //mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _location.latitude.toString(), _location.longitude.toString())
        //savedPos = _location
        //database.updateLocation(_name, LocationDto(_location))
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, currentPos?.latitude.toString(), currentPos?.longitude.toString())
        savedPos = currentPos
        database.updateLocation(_name, LocationDto(currentPos!!))
    }

    override fun gyroValueUpdate(_degree : Float) {
        gyroAngle = (_degree + gyroAngle) / 2
        if (direction != null){
                arrow.rotate(direction!! + compassAngle)
        }
    }

    override fun compassValueUpdate(_degree : Float) {
        compassAngle = _degree
        compass.rotate(compassAngle)
    }

    override fun onResume() {
        super.onResume()
        mainScreenBinding.btnRegisterLocation.setOnClickListener {
            val intent = Intent(this, SaveLocationActivity::class.java)
            startActivityForResult(intent, saveLocationActivity)
        }

        mainScreenBinding.btnListLocations.setOnClickListener {
            val intent = Intent(this, ListLocationActivity::class.java)
            startActivityForResult(intent, listLocationActivity)
        }

        location.startLocationUpdates()
        gyroSensor.startListen()
        compassSensor.startListen()
        Log.d("testLog", "OnResume done")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == listLocationActivity && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra("value")
            database.getLocation(value!!){
                savedPos = it?.toLocation()
                mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, savedPos?.latitude.toString(), savedPos?.longitude.toString())
            }
        }
        else if (requestCode == saveLocationActivity && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra("value")
            location.getLocation(value!!)
        }
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