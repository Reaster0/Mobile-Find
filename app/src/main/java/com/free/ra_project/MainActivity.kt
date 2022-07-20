package com.free.ra_project

import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.media.MicrophoneInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.free.ra_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GyroInterface, CompassInterface {

    private lateinit var mainScreenBinding : ActivityMainBinding
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
        var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var gyroSensor = GyroSensor(this, sensorManager)
        var compassSensor = CompassSensor(this, sensorManager)
        compassSensor.startListen()
        gyroSensor.startListen()
    }

    override fun gyroValueUpdate(_degree : Float) {
        x = _degree
        mainScreenBinding.tvCurrentCoordinates.text = getString(R.string.angleDebug, x.toString())
        Utils().rotate(mainScreenBinding.ivDirectionArrow, x)
    }

    override fun compassValueUpdate(_degree : Float) {
        y = _degree
        mainScreenBinding.tvSavedCoordinates.text = getString(R.string.angleDebug, _degree.toString())
        Utils().rotate(mainScreenBinding.ivCompass, _degree + 180 - x)
    }
}