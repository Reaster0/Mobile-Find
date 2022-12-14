package com.free.ra_project

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.location.Location
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.free.ra_project.databinding.ActivityMainBinding
import kotlin.math.pow

import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Region
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
    private var bleInRange : Boolean = false
    private lateinit var arrow : Arrow
    private lateinit var altitudeArrow : AltitudeArrow
    private lateinit var compass : Compass
    private lateinit var sensorManager : SensorManager
    private lateinit var bleSensor : BleSensor
    private var savedBeaconBle : BeaconDto? = null
    private var beaconBle : Beacon? = null
    private lateinit var location : LocationSensor
    private lateinit var gyroSensor : GyroSensor
    private lateinit var compassSensor : CompassSensor
    private val database = FirebaseLocation()

    private var soundPool: SoundPool? = null
    private var soundID = 0

    private val saveLocationActivity = 0
    private val listLocationActivity = 1
    private val listBeaconActivity = 2
    private var startKalman = false
    private var kalmanLatitude = GPSKalmanFilter(0.5, 0.125, 1.0, 1.0)
    private var kalmanLongitude = GPSKalmanFilter(0.5, 0.125, 1.0, 1.0)


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
        bleSensor = BleSensor(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        }
        else {
            soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }
        soundID = soundPool!!.load(this, R.raw.beep, 1)
    }

    override fun locationValueUpdate(_location: Location) {
        if (currentPos == null) {
            currentPos = _location
        }
        else {
            //currentPos!!.latitude = (((currentPos!!.latitude + _location.latitude) / 2 * 10000000.0).roundToInt() / 10000000.0).toDouble() //round to 7 decimals
            //currentPos!!.longitude = (((currentPos!!.longitude + _location.longitude) / 2 * 10000000.0).roundToInt() / 10000000.0).toDouble() //round to 7 decimals
            currentPos!!.altitude = (((currentPos!!.altitude + _location.altitude) / 2 * 10000000000000.0).roundToLong() / 10000000000000.0).toDouble() //round to 13 decimals
            currentPos!!.time = _location.time
        }
        val initStateLat = _location.latitude
        val initStateLong = _location.longitude

        if (startKalman) {
            kalmanLatitude.correct(initStateLat)
            kalmanLongitude.correct(initStateLong)
            currentPos!!.latitude = kalmanLatitude.state.roundTo(7)
            currentPos!!.longitude = kalmanLongitude.state.roundTo(7)
        }
        else {
            kalmanLatitude.setState(initStateLat, 1.0)
            kalmanLongitude.setState(initStateLong, 1.0)
            startKalman = true
        }


        if (savedPos != null){
            direction = bearingTo(currentPos!!.latitude, currentPos!!.longitude, savedPos!!.latitude, savedPos!!.longitude).toFloat()
            distance = distanceKm(currentPos!!.latitude, savedPos!!.latitude, currentPos!!.longitude, savedPos!!.longitude, currentPos!!.altitude, savedPos!!.altitude).toFloat()
            arrow.colorize(distance.roundToInt())
            distance.toDouble().roundTo(2)//((distance * 10.0).roundToInt() / 10.0).toFloat() // round to 2 decimal places
            val diffAltitude : Int = (savedPos!!.altitude - currentPos!!.altitude).toInt()
            altitudeArrow.rotate(diffAltitude.toFloat())

            if (!bleInRange){
                if (distance < 3.5f) {
                    mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, "<3.5m")
                    arrow.transform(true)
                }
                else {
                    arrow.transform(false)
                    mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, distance.toString() + "m")
                }
            }
        }
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.currentLocation, currentPos!!.latitude.toString(), currentPos!!.longitude.toString())
    }

    fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
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

        mainScreenBinding.btnListBeacons.setOnClickListener {
            val intent = Intent(this, ListBeaconActivity::class.java)
            startActivityForResult(intent, listBeaconActivity)
        }

        location.startLocationUpdates()
        gyroSensor.startListen()
        compassSensor.startListen()
        bleSensor.startRanging(this) { bleRanging(it) }
        bleSensor.startMonitoring(this) { bleMonitor(it) }
        Log.d("testLog", "OnResume done")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == listLocationActivity && resultCode == Activity.RESULT_OK) {
            val value = data?.getStringExtra("value")
            database.getLocation(value!!){ it ->
                savedPos = it?.toLocation()
                savedBeaconBle = it?.toBeaconInfo()
                bleInRange = false
                if (savedBeaconBle != null){
                    bleSensor.newTarget(this, savedBeaconBle!!)
                    bleSensor.startMonitoring(this){ bleMonitor(it) }
                    bleSensor.startRanging(this) { bleRanging(it) }
                }
                else {
                    bleSensor.stopRanging(this)
                    bleSensor.stopMonitoring(this)
                }
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
            savedBeaconBle = BeaconDto(beaconBle?.identifiers?.get(0).toString(), beaconBle?.identifiers?.get(1).toString(), beaconBle?.identifiers?.get(2).toString())
            if (bleInRange && savedBeaconBle != null) {
                database.updateLocation(value!!, LocationDto(currentPos!!, beaconBle))
                bleSensor.newTarget(this, savedBeaconBle!!)
                bleSensor.startMonitoring(this){ bleMonitor(it) }
                bleSensor.startRanging(this) { bleRanging(it) }
            }
            else
                database.updateLocation(value!!, LocationDto(currentPos!!, null))
        }
        else if (requestCode == listBeaconActivity && resultCode == Activity.RESULT_OK) {
            val value = data?.getSerializableExtra("value") as BeaconToSave
            savedBeaconBle = BeaconDto(value.uuid, value.major, value.minor)
//            bleSensor.newTarget(this, savedBeaconBle!!)
//            bleSensor.startMonitoring(this){ bleMonitor(it) }
//            bleSensor.startRanging(this) { bleRanging(it) }
            mainScreenBinding.tvSavedBeacon.text = getString(R.string.savedBeacon, savedBeaconBle?.uuid.toString())
        }
    }

    override fun alert(state : Int) {
        val emojiState: String
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

    private fun bleMonitor(state : Int) {
        bleInRange = state > 0
        mainScreenBinding.tvSavedInfo.text = getString(R.string.DistanceDebug, "")
    }

    private fun bleRanging(beacons : Collection<Beacon>) {
        for (beacon in beacons) {
            bleInRange = true
            if (beacon.distance < 10.0)
                arrow.transform(true)
            beaconBle = beacon
            mainScreenBinding.tvSavedInfo.text = ((beacon.distance * 100.0).toInt() / 100.0).toFloat().toString() + "m\uD83D\uDCF6" + "\nUuid:\n" + beacon.identifiers.get(0).toString()
            soundPool?.play(soundID, 1F, 1F, 0, 1, 1F + ((beaconBle!!.distance)!!.toFloat() / 5F))
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