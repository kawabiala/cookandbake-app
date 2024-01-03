package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R

class ManageAccountActivity : BaseActivity(), RequirePasswordFragment.RequirePasswordListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_manage_account)

        auth = Firebase.auth

        findViewById<TextView>(R.id.maUnsubscribeView).setOnClickListener {
            if (auth.currentUser == null) {
                return@setOnClickListener
            }

            val requirePasswordFragment = RequirePasswordFragment()
            requirePasswordFragment.arguments = Bundle().apply {
                putString("message", getString(R.string.confirmUnsubscribe))
            }
            requirePasswordFragment.show(supportFragmentManager, "requirePasswordManager")
        }

        findViewById<TextView>(R.id.maChangePasswordView).setOnClickListener {
            if (auth.currentUser == null) {
                return@setOnClickListener
            }

            //startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<TextView>(R.id.maLoginView).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.maLogoutView).setOnClickListener {
            if (auth.currentUser == null) {
                return@setOnClickListener
            }

            auth.signOut()
        }
//TODO Dataprotection
        findViewById<TextView>(R.id.maImpressumView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
        }

        findViewById<TextView>(R.id.maDatenschutzView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
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

    override fun onResume() {
        super.onResume()
        fillEmail()
    }

    override fun onPositiveButton(password: String?) {
        if (password == null) return

        //TODO delete account + all data
//        authenticationViewModel.unsubscribe(password)
    }

    override fun onNegativeButton() {
        // Do nothing
    }

    private fun alert(msg: String) {
        AlertDialog.Builder(this).apply {
            setMessage(msg)
            setPositiveButton("Ok") { _, _ ->
                // do nothing
            }
        }
    }

    private fun fillEmail() {
        val emailText = when {
            auth.currentUser != null -> {
                resources.getString(R.string.is_logged_in, auth.currentUser!!.email)
            }
            else -> {
                resources.getString(R.string.no_account)
            }
        }

        findViewById<TextView>(R.id.maEmailView).text = emailText
    }
}
