package com.free.ra_project

import android.graphics.Color
import android.widget.ImageView
import androidx.core.graphics.ColorUtils

class Arrow(_image: ImageView) {
    private var image: ImageView = _image

    fun rotate(_degree: Float) {
        if (image.rotation + _degree >= 360)
            image.rotation = (image.rotation + _degree) - 360
        image.apply { rotation += _degree }
    }

    fun colorize(_distance: Int) {
        val maxRedDistance = 100f // <- max distance far away
        val ratio: Float = if (_distance > maxRedDistance)
            0f
        else
            1 / (maxRedDistance / _distance)
        image.setColorFilter(
            ColorUtils.blendARGB(
                Color.parseColor("#2C2C2C"),
                Color.parseColor("#2196F3"),
                1 - ratio)) //between 0 and 1, 0 is 100% red, 1 is 100% blue
    }
}
