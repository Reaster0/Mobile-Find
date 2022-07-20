package com.free.ra_project

import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.ColorUtils

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

class Compass(_image: ImageView) : DirectionObjects(_image) {}
