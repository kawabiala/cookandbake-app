package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.pingwinek.jens.cookandbake.R

class ConfirmDialogFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var listener: ConfirmDialogListener
    private var confirmItemId: String? = null
    private var message: String = "You should provide a message with setArguments"

    override fun setArguments(args: Bundle?) {
        confirmItemId = args?.getString("remoteId")
        message = args?.getString("message") ?: ""
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
            builder.setPositiveButton(getString(R.string.delete)) { _, _ ->
                listener.onPositiveButton(confirmItemId)
            }
            builder.setNegativeButton(getString(R.string.close)) { _, _ ->
                listener.onNegativeButton(confirmItemId)
            }
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface ConfirmDialogListener {
        fun onPositiveButton(confirmItemId: String?)
        fun onNegativeButton(confirmItemId: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ConfirmDialogListener
        } catch (exception: ClassCastException) {
            throw ClassCastException("$context must implement ConfirmDialogListener")
        }
    }
}