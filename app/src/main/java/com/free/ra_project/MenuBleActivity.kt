package com.free.ra_project

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.lifecycle.Observer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region

class MenuBleActivity : AppCompatActivity() {
    val beaconManager = BeaconManager.getInstanceForApplication(this)
    val region = Region("all-beacons-region", null, null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_menu)
        beaconManager.getRegionViewModel(region).regionState.observe(this, monitoringObserver)

    }

    private val monitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.INSIDE) {
            Log.d(TAG, "Detected beacons(s)")
        }
        else {
            Log.d(TAG, "Stopped detecteing beacons")
        }
    }
}