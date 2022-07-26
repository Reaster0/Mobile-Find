package com.free.ra_project

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseLocation {
    private val database = FirebaseDatabase.getInstance()
    private val locations = database.getReference("locations")

    fun updateLocation(name : String, location: LocationDto){
        val ret = locations.child(name).setValue(location)
        println(ret)
    }


    fun getLocation(name: String, callback: (LocationDto?) -> Unit) {
        locations.child(name).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                callback(null)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val location = p0.getValue(LocationDto::class.java)
                callback(location)
            }
        })
    }

    fun getListLocations(callback: (Map<String?, LocationDto?>) -> Unit) {
        locations.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                callback(mapOf())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val map = p0.children.associate {
                    it.key to it.getValue(LocationDto::class.java)
                }
                callback(map)
            }
        })
    }

    fun deleteLocation(name: String){
        locations.child(name).removeValue()
    }
}