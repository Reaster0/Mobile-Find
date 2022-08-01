package com.free.ra_project

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import kotlin.math.roundToInt


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
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = GyroSensor(this, sensorManager)
        compassSensor = CompassSensor(this, sensorManager, currentPos)

        compassSensor.startListen()
        gyroSensor.startListen()
        arrow = Arrow(mainScreenBinding.ivDirectionArrow)
        altitudeArrow = AltitudeArrow(mainScreenBinding.ivAltitudeArrow, mainScreenBinding.tvDiffAltitude)
        compass = Compass(mainScreenBinding.ivCompass, mainScreenBinding.tvAngle)
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        location = LocationSensor(this, this)


        val intent = Intent(this, MenuBleActivity::class.java)
        startActivity(intent)
    }

    override fun locationValueUpdate(_location: Location) {
        currentPos = _location
        if (savedPos != null){
            direction = bearingTo(currentPos!!.latitude, currentPos!!.longitude, savedPos!!.latitude, savedPos!!.longitude).toFloat()
            distance = distanceKm(currentPos!!.latitude, savedPos!!.latitude, currentPos!!.longitude, savedPos!!.longitude, currentPos!!.altitude, savedPos!!.altitude).toFloat()
            arrow.colorize(distance!!.roundToInt())
            distance = ((distance * 10.0).roundToInt() / 10.0).toFloat() // round to 2 decimal places
            val diffAltitude : Float = (savedPos!!.altitude - currentPos!!.altitude).toFloat()
            altitudeArrow.rotate(diffAltitude)

            if (distance < 1.5f) {
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, "<1.5m")
                arrow.transform(true)
            }
            else {
                arrow.transform(false)
                mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString() + "m")
            }
        }
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, currentPos!!.latitude.toString(), currentPos!!.longitude.toString())
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
            if (currentPos != null)
                startActivityForResult(intent, saveLocationActivity)
        }

        mainScreenBinding.btnListLocations.setOnClickListener {
            val intent = Intent(this, ListLocationActivity::class.java)
            startActivityForResult(intent, listLocationActivity)
        }
        mainScreenBinding.btnMenuBle.setOnClickListener {
            val intent = Intent(this, MenuBleActivity::class.java)
            startActivity(intent)
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
                mainScreenBinding.tvLocationName.text = value
            }
        }
        else if (requestCode == saveLocationActivity && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra("value")
            //location.getLocation(value!!)
            mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, currentPos?.latitude.toString(), currentPos?.longitude.toString())
            mainScreenBinding.tvLocationName.text = value
            savedPos = currentPos
            database.updateLocation(value!!, LocationDto(currentPos!!))
        }
    }

    override fun alert(state : Int) {
        var emojiState: String
        if (state == 0 || state == 1)
            emojiState = "\uD83D\uDD34"
        else if (state == 1)
            emojiState = "\uD83D\uDFE0"
        else if (state == 2)
            emojiState = "\uD83D\uDFE1"
        else
            emojiState = "\uD83D\uDFE2"
        mainScreenBinding.tvPrecision.text = "prec. : " + state.toString() + "/3 " + emojiState
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