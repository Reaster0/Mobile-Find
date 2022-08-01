package com.free.ra_project

class KalmanLatLong
{
    private val minAccuracy: Float = 1f

    private var qMpS: Float = 0f
    private var TimeStamp_milliseconds: Long = 0
    private var lat: Double = 0.toDouble()
    private var lng: Double = 0.toDouble()
    private var variance: Float = 0.toFloat()
    // P matrix.  Negative means object uninitialised.  NB: units irrelevant, as long as same units used throughout

    fun KalmanLatLong(qMpS: Float)
    {
        this.qMpS = qMpS
        variance = -1f
    }

    fun get_TimeStamp(): Long { return TimeStamp_milliseconds }
    fun get_lat(): Double { return lat }
    fun get_lng(): Double { return lng }
    fun get_accuracy(): Float { return Math.sqrt(variance.toDouble()).toFloat() }

    fun SetState(lat: Double, lng: Double, accuracy: Float, TimeStamp_milliseconds: Long)
    {
        this.lat = lat
        this.lng = lng
        variance = accuracy * accuracy
        this.TimeStamp_milliseconds = TimeStamp_milliseconds
    }


    fun process(lat_measurement: Double, lng_measurement: Double, accuracy: Float, TimeStamp_milliseconds: Long)
    {
        var innerAccuracy = accuracy
        if (innerAccuracy < minAccuracy) innerAccuracy = minAccuracy

        if (variance < 0)
        {
            this.TimeStamp_milliseconds = TimeStamp_milliseconds
            lat = lat_measurement
            lng = lng_measurement
            variance = innerAccuracy * innerAccuracy
        }
        else
        {
            val timeIncMilliseconds = TimeStamp_milliseconds - this.TimeStamp_milliseconds
            if (timeIncMilliseconds > 0)
            {
                variance += timeIncMilliseconds.toFloat() * qMpS * qMpS / 1000
                this.TimeStamp_milliseconds = TimeStamp_milliseconds
            }
            val kalmanMatrix = variance / (variance + innerAccuracy * innerAccuracy)

            lat += kalmanMatrix * (lat_measurement - lat)
            lng += kalmanMatrix * (lng_measurement - lng)
            variance *= (1 - kalmanMatrix)
        }
    }
}
