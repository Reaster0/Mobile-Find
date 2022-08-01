package com.free.ra_project

import android.app.Activity
import android.app.Application
import android.hardware.*
import android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH
import android.location.Location
import android.util.Log
import android.view.View
import com.google.firebase.database.core.Context
import java.security.AccessControlContext
import java.security.AccessController.getContext

interface CompassInterface {
    fun compassValueUpdate(_degree : Float)
    fun alert(state : Boolean)
}

class CompassSensor(_compassInterface : CompassInterface, _sensorManager : SensorManager, _location : Location?) : SensorEventListener {
    private var compassInterface : CompassInterface = _compassInterface
    private val sensorManager: SensorManager = _sensorManager
    private val compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val location : Location? = _location
    private var time = System.currentTimeMillis()
    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)

    fun startListen() {
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopListen() {
        sensorManager.unregisterListener(this)
    }

    private var rotationMatrix = FloatArray(9)
    private var lastMagneCopied = false
    private var lastAccelCopied = false

    override fun onSensorChanged(p0: SensorEvent?) {
        var alpha = 0.98f

        if (p0!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * p0.values[0]
            geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * p0.values[1]
            geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * p0.values[2]
            //geomagnetic = p0.values
            lastMagneCopied = true
        }
        if (p0.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * p0.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * p0.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * p0.values[2]
            //gravity = p0.values
            lastAccelCopied = true
        }

        if (lastAccelCopied && lastMagneCopied) {
            SensorManager.getRotationMatrix(rotationMatrix, null ,gravity, geomagnetic)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            var degree = Math.toDegrees(orientation[0].toDouble()).toFloat()
            if (location != null)
                degree -= GeomagneticField(location.latitude.toFloat(), location.longitude.toFloat(), location.altitude.toFloat(), time).declination
            compassInterface.compassValueUpdate(-degree)
        }

//        val rotationMatrix: FloatArray = FloatArray(16)
//        val orientation: FloatArray = FloatArray(3)
//        SensorManager.getRotationMatrixFromVector(rotationMatrix, p0!!.values)
//        var angle = (Math.toDegrees(
//            SensorManager.getOrientation(
//                rotationMatrix,
//                orientation
//            )[0].toDouble() + 360
//        ) % 360).toFloat()
//        if (location != null) {
//            angle -= GeomagneticField(location.latitude.toFloat(), location.longitude.toFloat(), location.altitude.toFloat(), time).declination
//        }
//        compassInterface.compassValueUpdate(angle)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        if (p1 != SENSOR_STATUS_ACCURACY_HIGH) {
            compassInterface.alert(true)
        }
        Log.d("testLog", "Accuracy: $p1")
    }
}