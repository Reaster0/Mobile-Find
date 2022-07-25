package com.free.ra_project

import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.*


class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface, LocationInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    private var gyroAngle = 0.0f
    private var compassAngle = 0.0f
    private var currentPos : Location? = null
    private var savedPos : Location? = null
    private var direction : Float? = null
    private var distance : Float = 0.0f
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


    fun distanceKm(
        lat1: Double, lat2: Double, lon1: Double,
        lon2: Double, el1: Double, el2: Double
    ): Double {
        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var dist = R * c * 1000 // convert to meters
        val height = el1 - el2
        dist = dist.pow(2.0) + height.pow(2.0)
        return sqrt(dist)
    }

    override fun locationValueUpdate(_location: Location) {

        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, _location.latitude.toString(), _location.longitude.toString())
        currentPos = _location
        if (savedPos != null){
            if (direction != null) {
                direction = currentPos!!.bearingTo(savedPos!!)

                distance = distanceKm(currentPos!!.latitude, savedPos!!.latitude, currentPos!!.longitude, savedPos!!.longitude, currentPos!!.altitude, savedPos!!.altitude).toFloat()
                arrow.colorize(distance!!.roundToInt())
                distance = ((distance * 100.0).roundToInt() / 100.0).toFloat() // round to 2 decimal places

                if (distance < 1.5f)           //distance = if (distance == null) currentPos!!.distanceTo(savedPos!!) else (currentPos!!.distanceTo(savedPos!!) + distance!!) / 2
                    mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, "<1.5m")
                else
                    mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString() + "m")
            }
            else
                direction = currentPos!!.distanceTo(savedPos!!)
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
        compassAngle = _degree
        compass.rotate(compassAngle)
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