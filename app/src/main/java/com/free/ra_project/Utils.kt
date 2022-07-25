package com.free.ra_project

import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.ColorUtils

import android.widget.TextView


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

class AltitudeArrow(_image: ImageView, _text: TextView) : DirectionObjects(_image) {
    var diffAltitudeText: TextView = _text

    fun rotate(_altitudeDiff: Int) {
        if (_altitudeDiff > 0)
            image.apply { rotation = 0f }
        else if (_altitudeDiff < -0)
            image.apply { rotation = 180f }
        else
            image.apply { rotation = 90f }
        diffAltitudeText.text = "⇵" + _altitudeDiff.toString() + "m"
    }
}

class Compass(_image: ImageView) : DirectionObjects(_image) {}
