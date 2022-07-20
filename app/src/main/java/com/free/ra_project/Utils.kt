package com.free.ra_project

import android.widget.ImageView

class Utils {
    fun rotate(image:ImageView, degree: Float) {
        image.apply { rotation = degree }
    }
}