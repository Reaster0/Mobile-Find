package com.free.ra_project

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.ActivityResult

class ListLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_location)

        val buttonClick = findViewById<Button>(R.id.btnBack)
        buttonClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("value", "button back")
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        FirebaseLocation().getListLocations { locations ->
            val mListView = findViewById<ListView>(R.id.list_view)
            mListView.adapter = ArrayAdapter<String>(
                this,
                R.layout.list_locations,
                R.id.text_view,
                locations.map { it.key }.toTypedArray()
            )

            mListView.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as String
                Log.d("ListLocationActivity", "selected item == $selectedItem")
                intent.putExtra("value", selectedItem.toString())
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }
    }
}