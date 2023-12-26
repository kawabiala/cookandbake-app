package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

//class ConfirmDialogFragment : androidx.fragment.app.DialogFragment() {
class ConfirmDialogFragment(
    val onPositiveButton: (String?) -> Unit,
    val onNegativeButton: (String?) -> Unit
) : AppCompatDialogFragment() {

    private lateinit var listener: ConfirmDialogListener
    private var confirmItemId: String? = null
    private var message: String = "You should provide a message with setArguments"
    private var posButtonText = "Ok"
    private var negButtonText = "Cancel"

    override fun setArguments(args: Bundle?) {
        confirmItemId = args?.getString("confirmItemId")
        message = args?.getString("message") ?: ""
        posButtonText = args?.getString("posButtonText") ?: posButtonText
        negButtonText = args?.getString("negButtonText") ?: negButtonText
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
            builder.setPositiveButton(posButtonText) { _, _ ->
                onPositiveButton(confirmItemId)
                //listener.onPositiveButton(confirmItemId)
            }
            builder.setNegativeButton(negButtonText) { _, _ ->
                onNegativeButton(confirmItemId)
                //listener.onNegativeButton(confirmItemId)
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