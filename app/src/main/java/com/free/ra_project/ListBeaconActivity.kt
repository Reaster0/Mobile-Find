package com.free.ra_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.altbeacon.beacon.Beacon

class ListBeaconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_beacon)

        val buttonClick = findViewById<Button>(R.id.btnBack)
        buttonClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("value", "button back")
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        BleSensor(this).startRanging(this) { beacons ->
            val list = mutableListOf<BeaconToSave>()
            for (beacon in beacons) {
//                val dto = BeaconDto(beacon.id1.toString(), beacon.id2.toString(), beacon.id3.toString())
//                val distanceLong = beacon.distance
//                val uuid = beacon.id1
//                val distanceString:String = "$uuid ($distanceLong meters)"
                val tmpBeacon = BeaconToSave(beacon.id1.toString(), beacon.id2.toString(), beacon.id3.toString(), beacon.distance.toString())
                list.add(tmpBeacon)
            }
            val adapter = BeaconListAdapter(this, list)
            val mListView = findViewById<ListView>(R.id.list_view)
            mListView.adapter = adapter

            mListView.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as BeaconToSave
                Log.d("ListBeaconActivity", "selected item == $selectedItem")
                intent.putExtra("value", selectedItem)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}