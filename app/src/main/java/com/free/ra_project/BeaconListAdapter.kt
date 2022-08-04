package com.free.ra_project

import android.widget.ArrayAdapter
import org.altbeacon.beacon.Beacon
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlin.Int

class BeaconListAdapter(context: Context, objects: List<Beacon>) :
    ArrayAdapter<Beacon>(context, 0, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var beacon : Beacon = getItem(position)!!
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_beacons, parent, false)
        }
        var str = """${beacon.id1} (${beacon.distance} meters)"""
        (convertView?.findViewById(R.id.text_view) as TextView).setText(str)
        return convertView
    }
}