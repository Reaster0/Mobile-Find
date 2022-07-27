package com.free.ra_project

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.result.ActivityResult

class SaveLocationActivity : AppCompatActivity() {

    var mEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_location)

        val btnCancel = findViewById<Button>(R.id.btnBack)
        val btnSave = findViewById<Button>(R.id.btnSave)
        mEditText = findViewById<EditText>(R.id.et_location_name)

        btnSave.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (mEditText?.text?.isEmpty() == true) {
                intent.putExtra("value", "button back")
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                intent.putExtra("value", mEditText?.text.toString())
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("value", "button back")
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

    }


}