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
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel

class RegisterActivity : BaseActivity() {

    //private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_register)

        auth = Firebase.auth
        /*
        authenticationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(AuthenticationViewModel::class.java)

        authenticationViewModel.response.observe(this) { response ->
            when (response.action) {
                AuthService.AuthenticationAction.REGISTER -> {
                    if (response.code == 200) {
                        setMessage(resources.getString(R.string.confirmationSent))
                    } else {
                        setMessage(resources.getString(R.string.registrationFailed))
                    }
                }
                AuthService.AuthenticationAction.CONFIRM -> {
                    if (response.code == 200) {
                        setMessage(resources.getString(R.string.confirmationSucceeded))
                    } else {
                        setMessage(resources.getString(R.string.confirmationFailed))
                    }
                }
                else -> {}
            }
        }
         */

        findViewById<TextView>(R.id.raAcceptance).apply {
            text = getSpannableAcceptanceText()
            movementMethod = LinkMovementMethod.getInstance()
        }

        optionMenu.apply {
            addMenuEntry(
                R.id.OPTION_MENU_CLOSE,
                resources.getString(R.string.close),
                R.drawable.ic_action_close_black,
                true
            ) {
                finish()
                true
            }
        }
    }

    fun registerButton(view: View) {
        deleteMessage()

        //if (authenticationViewModel.hasStoredAccount()) {
        if (auth.currentUser != null) {
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.confirmLogout))
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    logout()
                    register()
                }
                setNegativeButton(getString(R.string.no)) { _, _ -> }
                create()
                show()
            }
        } else {
                register()
        }

    }

    private fun logout() {
        //authenticationViewModel.logout()
        auth.signOut()
    }

    private fun register() {
        /*
        authenticationViewModel.register(
            findViewById<TextView>(R.id.raEmail).text.toString(),
            findViewById<TextView>(R.id.raPassword).text.toString(),
            findViewById<CheckBox>(R.id.raCheckBox).isChecked
        )

         */
        auth.createUserWithEmailAndPassword(
            findViewById<TextView>(R.id.raEmail).text.toString(),
            findViewById<TextView>(R.id.raPassword).text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                setMessage("Registration successful")
            } else {
                setMessage("Registration failed")
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
                    .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
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
