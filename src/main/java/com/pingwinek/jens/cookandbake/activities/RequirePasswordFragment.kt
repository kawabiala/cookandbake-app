package com.pingwinek.jens.cookandbake.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.pingwinek.jens.cookandbake.R

class RequirePasswordFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var listener: RequirePasswordListener
    private var message: String = "You should provide a message with setArguments"

    override fun setArguments(args: Bundle?) {
        message = args?.getString("message") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            buildDialog(it)
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface RequirePasswordListener {
        fun onPositiveButton(password: String?)
        fun onNegativeButton()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as RequirePasswordListener
        } catch (exception: ClassCastException) {
            throw ClassCastException("$context must implement RequirePasswordListener")
        }
    }

    @SuppressLint("InflateParams")
    private fun buildDialog(activity: Activity) : Dialog {
        val builder = AlertDialog.Builder(activity).apply {
            setMessage(message)

            val dialogView = activity.layoutInflater.inflate(R.layout.dialog_view_password, null)
            setView(dialogView)

            val pwView = dialogView.findViewById<EditText>(R.id.dvPasswordView)
            setPositiveButton("Löschen") { _, _ ->
                listener.onPositiveButton(pwView.text.toString())
            }

            setNegativeButton("Abbrechen") { _, _ ->
                listener.onNegativeButton()
            }
        }
        return builder.create()
    }
}