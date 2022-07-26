package com.free.ra_project

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


        val mItems: Array<String> = arrayOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Zero")


        val mListView = findViewById<ListView>(R.id.list_view)
        mListView.adapter = ArrayAdapter<String>(this,
            R.layout.list_locations,
            R.id.text_view,
            mItems
        )
    }
}