package com.free.ra_project

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class Dialog: DialogFragment() {
    private var mEditText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View =  inflater.inflate(R.layout.dialog, container, false)

        mEditText = view.findViewById<EditText>(R.id.et_location_name)

        val negative = view.findViewById<Button>(R.id.btnNegative)
        negative.setOnClickListener {
            dismiss()
        }

        val positive = view.findViewById<Button>(R.id.btnPositive)
        positive.setOnClickListener {
            // SAVE LOCATION HERE
            // SAVE LOCATION HERE
            // SAVE LOCATION HERE
            // SAVE LOCATION HERE
            // SAVE LOCATION HERE
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()

        // width/height of dialog
        dialog!!.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // change opacity of dialog background
        dialog?.window?.also { window ->
            window.attributes?.also { attributes ->
                attributes.dimAmount = 0.8f
                window.attributes = attributes
            }
        }
        // color of dialog background
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}
