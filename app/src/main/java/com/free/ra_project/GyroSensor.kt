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
    fun gyroValueUpdate(_x: Float?, _y: Float?, _z: Float?)
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
        if (p0!!.sensor.type == TYPE_ROTATION_VECTOR) {
            Log.d("reaster", p0!!.sensor.name)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, p0!!.values)

            SensorManager.getOrientation(remapRotationMatrix, orientation)
            for (i in 0..2) {
            }
            var angle = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0].toDouble() + 360) % 360).toFloat()
            Log.d(
                "gyro",
                "X: ${p0?.values?.get(0)}, Y: ${p0?.values?.get(1)}, Z: ${p0?.values?.get(2)}"
            )
            interGyro.gyroValueUpdate(angle, orientation[1], orientation[2])
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("reaster", "onAccuracyChanged")
    }

}