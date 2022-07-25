package com.free.ra_project

import android.content.Context
import android.hardware.SensorManager
import android.icu.text.Transliterator
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt


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
    private val database = FirebaseDatabase.getInstance().getReference("location")

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
        compass = Compass(mainScreenBinding.ivCompass)

        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")

        location = LocationSensor(this, this)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(LocationDto::class.java)
                if (value != null) {
                    val resultDto = Location("")
                    resultDto.latitude = value.latitude
                    resultDto.longitude = value.longitude
                    resultDto.altitude = value.altitude
                    resultDto.time = value.time
                    mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, value.latitude.toString(), value.longitude.toString())
                    savedPos = resultDto
                }
                else {
                    mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "", "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", error.toException())
            }
        })
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

    data class LocationDto(private var source: Location) {
        constructor() : this(Location(""))
        val latitude : Double = source.latitude
        val longitude : Double = source.longitude
        val altitude : Double = source.altitude
        val time : Long = source.time

        fun toLocation() : Location {
            return Location("").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.altitude = altitude
                this.time = time
            }
        }
    }

    override fun locationValueRequested(_location : Location) {
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, _location.latitude.toString(), _location.longitude.toString())
        savedPos = _location
        database.setValue(LocationDto(_location))
    }

    override fun gyroValueUpdate(_degree : Float) {
        gyroAngle = (_degree + gyroAngle) / 2
        if (direction != null){
                arrow.rotate(direction!! - compassAngle - gyroAngle)
        }
    }

    override fun compassValueUpdate(_degree : Float) {
        compassAngle = (_degree + compassAngle) / 2
        compass.rotate(compassAngle - 180 - gyroAngle)
    }

    override fun onResume() {
        super.onResume()
        mainScreenBinding.btnRegisterLocation.setOnClickListener {
            location.getLocation()
            mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, savedPos?.latitude.toString(), savedPos?.longitude.toString())
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