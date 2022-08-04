package com.free.ra_project

class GPSKalmanFilter(_q: Double, _r: Double, _f: Double = 1.0, _h: Double = 1.0) {
    var x0: Double = 0.0 // predicted state
    var p0: Double = 0.0 // predicted covariance

    var f: Double = _f // factor of real value to previous real value
    var q: Double = _q // measurement noise
    var h: Double = _h // factor of measured value to real value
    var r: Double = _r // environment noise

    var state: Double = 0.0
    var covariance: Double = 0.0

    fun  setState(_state : Double, _covariance : Double)
    {
        state = _state
        covariance = _covariance
    }

    fun correct(data : Double)
    {
        //time update - prediction
        x0 = f * state
        p0 = f * covariance * f + q

        //measurement update - correction
        val k = h * p0 / (h * p0 * h + r)
        state = x0 + k * (data - h * x0)
        covariance = (1 - k * h) * p0
    }
}