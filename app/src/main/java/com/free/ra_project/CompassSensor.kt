package com.free.ra_project

import android.hardware.*
import android.location.Location
import android.util.Log

interface CompassInterface {
    fun compassValueUpdate(_degree : Float)
}

class CompassSensor(_compassInterface : CompassInterface, _sensorManager : SensorManager, _location : Location?) : SensorEventListener {
    private var compassInterface : CompassInterface = _compassInterface
    private val sensorManager: SensorManager = _sensorManager
    private val compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    private val location : Location? = _location
    private var time = System.currentTimeMillis()


    fun startListen() {
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListen() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val rotationMatrix: FloatArray = FloatArray(16)
        val remapRotationMatrix: FloatArray = FloatArray(16)
        val orientation: FloatArray = FloatArray(3)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, p0!!.values)
        SensorManager.getOrientation(remapRotationMatrix, orientation)
        var angle = (Math.toDegrees(
            SensorManager.getOrientation(
                rotationMatrix,
                orientation
            )[0].toDouble() + 360
        ) % 360).toFloat()
        if (location != null) {
            angle -= GeomagneticField(location.latitude.toFloat(), location.longitude.toFloat(), location.altitude.toFloat(), time).declination
        }
        compassInterface.compassValueUpdate(angle)
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("reaster", "onAccuracyChanged")
    }
}