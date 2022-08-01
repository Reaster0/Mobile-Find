package com.free.ra_project

import android.app.AlertDialog
import android.content.Context
import java.security.AccessControlContext
import java.security.AccessController.getContext


class SimpleDialog(val cont: Context?) {
    private var alert: AlertDialog? = null

    fun run(_mode: Boolean, _message: String?) {
        if (_mode) {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(cont)

            // set message of alert dialog
            dialogBuilder.setMessage(_message)
                // if the dialog is cancelable
                .setCancelable(true)

            // create dialog box
            alert = dialogBuilder.create()
            // show alert dialog
            alert!!.show()
        } else {
            alert!!.dismiss()
        }
    }
}