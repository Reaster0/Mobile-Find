package com.free.ra_project

import android.Manifest
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.free.ra_project.databinding.ActivityBleMenuBinding
import com.free.ra_project.databinding.ActivityMainBinding
import org.altbeacon.beacon.*

class MenuBleActivity : AppCompatActivity() {
    private lateinit var region: Region
    private lateinit var mainScreenBinding: ActivityBleMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityBleMenuBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        val beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)
        beaconManager.beaconParsers.add(
            BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        )
        region = Region("all-beacons-region", null, null, null)
        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)
        val regionViewModel =
            BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
        regionViewModel.regionState.observeForever(centralMonitoringObserver)
        regionViewModel.rangedBeacons.observeForever(centralRangingObserver)

        //beaconManager.getRegionViewModel(region).rangedBeacons.observe(this, rangingObserver)
        //beaconManager.startRangingBeacons(region)
    }

    private val centralMonitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.OUTSIDE) {
            mainScreenBinding.tvBleDebug1.text = "outside beacon region: " + region
            Log.d("reaster", "outside beacon region: " + region)
        } else {
            mainScreenBinding.tvBleDebug1.text = "inside beacon region: " + region
            Log.d("reaster", "inside beacon region: " + region)
        }
    }

        private val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
            Log.d("reaster", "Ranged: ${beacons.count()} beacons")
            //mainScreenBinding.tvBleDebug2.text = "Ranged: ${beacons.count()} beacons"
            for (beacon: Beacon in beacons) {
                if (beacon.bluetoothName == "iBKS105")
                    mainScreenBinding.tvBleDebug2.text = "Distance: ${beacon.distance}m" //mainScreenBinding.tvBleDebug2.text = "${beacon.bluetoothName} about ${beacon.distance} meters away"
                Log.d("reaster", "$beacon about ${beacon.distance} meters away")
            }
        }
}