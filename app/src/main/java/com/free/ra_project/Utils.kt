package com.free.ra_project

import android.widget.ImageView

class Utils {
    fun rotate(image:ImageView, degree: Float) {
        if (image.rotation + degree >= 360)
            image.rotation = (image.rotation + degree) - 360
        image.apply { rotation += degree }
    }
}