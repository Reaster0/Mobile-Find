package com.free.ra_project

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService

interface GyroInterface {
    fun gyroValueUpdate(_x: Float?, _y: Float?, _z: Float?)
}


class GyroSensor(_interGyro: GyroInterface, _sensorManager: SensorManager) : SensorEventListener {
    private var interGyro : GyroInterface = _interGyro
    private var sensorManager: SensorManager = _sensorManager
    private var gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


    fun startListen(){
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListen(){
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        interGyro.gyroValueUpdate(p0?.values?.get(0), p0?.values?.get(1), p0?.values?.get(2))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

}