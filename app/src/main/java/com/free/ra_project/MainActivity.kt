package com.free.ra_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.free.ra_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainScreenBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreenBinding.root)
    }

    fun test() {
        return
    }
}