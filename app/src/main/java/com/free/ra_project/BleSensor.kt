package com.free.ra_project

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.altbeacon.beacon.*

class BleSensor(private val context: Context) {
    private var region: Region
    private var beaconManager: BeaconManager
    private var regionViewModel: RegionViewModel

    init {
        val bluetoothAdapter: BluetoothAdapter = context.getSystemService(BluetoothManager::class.java).adapter
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(context as Activity, enableBtIntent, 1, null)
        }
        beaconManager = BeaconManager.getInstanceForApplication(context)
        BeaconManager.setDebug(true)
        beaconManager.beaconParsers.add(
            BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24") //ibeacon layout
        )
        //region = Region("blueBoy", Identifier.parse("00000000-0000-0000-0000-000000000000", 16), Identifier.parse("57", 2), Identifier.parse("57", 2))
        region = Region("all-beacons", null, null, null)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        regionViewModel = BeaconManager.getInstanceForApplication(context).getRegionViewModel(region)
    }

    fun startRanging(lifeCycle: LifecycleOwner, callback: (Collection<Beacon>) -> Unit) {
        regionViewModel.rangedBeacons.observe(lifeCycle, callback)
    }

    fun stopRanging(lifeCycle: LifecycleOwner) {
        regionViewModel.rangedBeacons.removeObservers(lifeCycle)
    }

    fun startMonitoring( lifeCycle: LifecycleOwner, callback: (Int) -> Unit) {
        regionViewModel.regionState.observe(lifeCycle, callback)
    }

    fun stopMonitoring(lifeCycle: LifecycleOwner) {
        regionViewModel.regionState.removeObservers(lifeCycle)
    }

    fun newTarget(lifeCycle: LifecycleOwner, beacon : BeaconDto){
        stopMonitoring(lifeCycle)
        stopRanging(lifeCycle)
        region = Region("blueBoy", Identifier.parse(beacon.uuid, 16), Identifier.parse(beacon.major, 2), Identifier.parse(beacon.minor, 2))
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        regionViewModel = BeaconManager.getInstanceForApplication(context).getRegionViewModel(region)
    }
}