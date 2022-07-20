package com.free.ra_project

import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.media.MicrophoneInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.free.ra_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GyroInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    var x = 2.0f
    var y = 3.0f
    var z = x + y

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, "lolilol", "lolilol2")

        var gyroSensor = GyroSensor(this, sensorManager)
        gyroSensor.startListen()
        Log.d("test", "x : $x, y : $y, z : $z")
    }

    fun test() {
        return
    }

    override fun gyroValueUpdate(_x: Float?, _y: Float?, _z: Float?) {
        x = _x!!
        y = _y!!
        z = _z!!
        Log.d("test", "x : $x, y : $y, z : $z")
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.savedLocation, x.toString(), y.toString())
    }
}