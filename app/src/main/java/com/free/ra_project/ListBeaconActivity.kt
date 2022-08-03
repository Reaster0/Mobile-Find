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
            val list = mutableListOf<Beacon>()
            for (beacon in beacons) {
//                val dto = BeaconDto(beacon.id1.toString(), beacon.id2.toString(), beacon.id3.toString())
//                val distanceLong = beacon.distance
//                val uuid = beacon.id1
//                val distanceString:String = "$uuid ($distanceLong meters)"
                list.add(beacon)
            }
            val mListView = findViewById<ListView>(R.id.list_view)
            mListView.adapter = ArrayAdapter<Beacon>(
                this,
                R.layout.list_beacons,
                R.id.text_view,
                list.toTypedArray()
            )

            mListView.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as String
                Log.d("ListBeaconActivity", "selected item == $selectedItem")
                intent.putExtra("value", selectedItem.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}