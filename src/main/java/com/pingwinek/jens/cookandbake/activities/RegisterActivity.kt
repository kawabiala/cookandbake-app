package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Annotation
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.DATAPROTECTIONPATH
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_register)

        findViewById<TextView>(R.id.raAcceptance).apply {
            text = getSpannableAcceptanceText()
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun getOptionsMenu(): OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_CLOSE, resources.getString(R.string.close)) {
                finish()
                true
            }.apply {
                iconId = R.drawable.ic_action_close
                ifRoom = true
            }
        }
    }

    fun registerButton(view: View) {
        deleteMessage()

        if (AuthService.getInstance(application).hasStoredAccount()) {
            AlertDialog.Builder(this).apply {
                setMessage("Soll der aktuelle Benutzer ausgeloggt werden?")
                setPositiveButton("Ja") { _, _ ->
                    AuthService.getInstance(application).logout { _, _ ->
                        register()
                    }
                }
                setNegativeButton("Nein") { _, _ -> }
                create()
                show()
            }
        } else {
            register()
        }

    }

    private fun register() {
        AuthService.getInstance(application).register(
            findViewById<TextView>(R.id.raEmail).text.toString(),
            findViewById<TextView>(R.id.raPassword).text.toString(),
            findViewById<CheckBox>(R.id.raCheckBox).isChecked
            ) { code, _ ->
            if (code == 201) {
                setMessage(resources.getString(R.string.confirmationSent))
            } else {
                setMessage(resources.getString(R.string.registrationFailed))
            }
        }
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.raMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.raMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun getSpannableAcceptanceText() : SpannableString {
        val clickableSpan = SimpleClickableSpan {
            startActivity(
                Intent(this@RegisterActivity, ImpressumActivity::class.java)
                    .putExtra("url", DATAPROTECTIONPATH))
        }

        val spannableAcceptanceText = SpannableString(resources.getText(R.string.declareAcceptanceOfDataprotection))
        val annotations = spannableAcceptanceText.getSpans(0, spannableAcceptanceText.length, Annotation::class.java)
        annotations.forEach {
            if (it.key == "clickableArea") {
                if (it.value == "dataprotection") {
                    spannableAcceptanceText.setSpan(
                        clickableSpan,
                        spannableAcceptanceText.getSpanStart(it),
                        spannableAcceptanceText.getSpanEnd(it),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return spannableAcceptanceText
    }
}

class SimpleClickableSpan(val action: () -> Unit) : ClickableSpan() {

    override fun onClick(widget: View) {
        action()
    }
}
