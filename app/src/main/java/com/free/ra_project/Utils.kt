package com.free.ra_project


import com.free.ra_project.databinding.ActivityMainBinding
import kotlin.math.atan2
import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.ColorUtils
import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

open class DirectionObjects(_image: ImageView) {
    var image: ImageView = _image

    fun rotate(_degree: Float) {
        image.apply { rotation = _degree }
    }
}

class Arrow(_image: ImageView) : DirectionObjects(_image) {

    fun colorize(_distance: Int) {
        val maxRedDistance = 100f // <- max distance far away
        val ratio: Float = if (_distance > maxRedDistance)
            0f
        else
            1 / (maxRedDistance / _distance)
        image.setColorFilter(
            ColorUtils.blendARGB(
                Color.parseColor("#9C9C9C"),
                Color.parseColor("#5BAAE8"),
                1 - ratio)) //between 0 and 1, 0 is 100% red, 1 is 100% blue
    }
}

class AltitudeArrow(_image: ImageView) : DirectionObjects(_image) {

    fun rotate(_altidude_diff: Int) {
        val altidudeApprox = 10 // <- max distance far away

        if (_altidude_diff > altidudeApprox)
            image.apply { rotation = 0f }
        else if (_altidude_diff < -altidudeApprox)
            image.apply { rotation = 180f }
        else
            image.apply { rotation = 90f }
    }
}

class Compass(_image: ImageView) : DirectionObjects(_image) {}
