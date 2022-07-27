package com.free.ra_project

import android.graphics.Color
import android.location.Location
import android.widget.ImageView
import androidx.core.graphics.ColorUtils
import kotlin.math.*

import android.widget.TextView


open class DirectionObjects(_image: ImageView) {
    var image: ImageView = _image

    fun rotate(_degree: Float) {
        image.apply { rotation = _degree }
    }

    fun transform(mode: Boolean) {
        if (mode)
            image.setImageResource(R.drawable.rond);
        else
            image.setImageResource(R.drawable.arrow);
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
        else {
            image.setImageResource(R.drawable.rond);
            image.apply { rotation = 90f }
        }
        diffAltitudeText.text = "⇵" + _altitudeDiff.toString() + "m"
    }
}

class Compass(_image: ImageView) : DirectionObjects(_image) {}

fun bearingTo(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLon = lon2 - lon1
    val x = sin(dLon) * cos(lat2)
    val y = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    return Math.toDegrees(atan2(x, y))
}

fun distanceKm(lat1: Double, lat2: Double, lon1: Double, lon2: Double, el1: Double, el2: Double): Double {
    val R = 6371 // ~Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var dist = R * c * 1000 // convert to meters
    val height = el1 - el2
    dist = dist.pow(2.0) + height.pow(2.0)
    return sqrt(dist)
}