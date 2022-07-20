package com.free.ra_project

import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

interface GyroInterface {
    fun gyroValueUpdate(_degree : Float)
}


class GyroSensor(_interGyro: GyroInterface, _sensorManager: SensorManager) : SensorEventListener {
    private var interGyro : GyroInterface = _interGyro
    private var sensorManager: SensorManager = _sensorManager
    private var gyroscope = sensorManager.getDefaultSensor(TYPE_ROTATION_VECTOR)


    fun startListen(){
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListen(){
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val rotationMatrix: FloatArray = FloatArray(16)
        val remapRotationMatrix: FloatArray = FloatArray(16)
        val orientation: FloatArray = FloatArray(3)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, p0!!.values)
        SensorManager.getOrientation(remapRotationMatrix, orientation)
        var angle = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0].toDouble() + 360) % 360).toFloat()
        interGyro.gyroValueUpdate(angle)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("reaster", "onAccuracyChanged")
    }

}