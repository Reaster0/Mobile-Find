package com.free.ra_project

import android.location.Location
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Identifier

data class BeaconDto(
    val uuid: String?,
    val major: String?,
    val minor: String?,
){
    constructor() : this(null, null, null)
}

data class LocationDto(private var source: Location, private var beacon: Beacon?) {
    constructor() : this(Location(""), null)
    val latitude : Double = source.latitude
    val longitude : Double = source.longitude
    val altitude : Double = source.altitude
    val time : Long = source.time
    val beaconBle : BeaconDto = BeaconDto(beacon?.identifiers?.get(0).toString(), beacon?.identifiers?.get(1).toString(), beacon?.identifiers?.get(2).toString())
    //val bleBeaconIdentifier : List<Identifier>? = beaconBle?.identifiers
    //val bleBeaconUuid : String = beaconBle?.identifiers.toString()

    fun toLocation() : Location {
        return Location("").apply {
            this.latitude = this@LocationDto.latitude
            this.longitude = this@LocationDto.longitude
            this.altitude = this@LocationDto.altitude
            this.time = this@LocationDto.time
        }
    }
    fun toBeaconInfo() : BeaconDto? {
        if (beaconBle.uuid == null || beaconBle.major == null || beaconBle.minor == null) {
            return null
        }
        return beaconBle
    }
}