package com.free.ra_project

import android.location.Location

data class LocationDto(private var source: Location) {
    constructor() : this(Location(""))
    val latitude : Double = source.latitude
    val longitude : Double = source.longitude
    val altitude : Double = source.altitude
    val time : Long = source.time

    fun toLocation() : Location {
        return Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
            this.altitude = altitude
            this.time = time
        }
    }
}