package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.pingwinek.jens.cookandbake.R

class LostPasswordActivity : BaseActivity() {

    //private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_lost_password)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email

        Log.i(this::class.java.name, "email: $email")
        Log.i(this::class.java.name, "anonymous: ${user?.isAnonymous}")
        Log.i(this::class.java.name, "email verified: ${user?.isEmailVerified}")

    /*
            val linkData = intent.data?.getQueryParameter("link")
            //val uri = Uri.parse(linkData).queryParameterNames

            //Log.i(this::class.java.name, "linkData: $uri")

            Log.i(this::class.java.name, "current User: ${auth.currentUser}")
            Log.i(this::class.java.name, "verificationLink $verificationLink")
            Log.i(this::class.java.name, "linkData $linkData")
    */

        val verificationLink = intent.data?.toString()

        if (email != null && verificationLink != null && auth.isSignInWithEmailLink(verificationLink)) {
            Log.i(this::class.java.name, "building credentials")
            val credential = EmailAuthProvider.getCredentialWithLink(email, verificationLink)
            auth.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Log.i(this::class.java.name, "successfully reauthenticated")
                    } else {
                        Log.i(this::class.java.name, "exception: ${task.exception}")
                    }
                }
        } else {
            Log.i(this::class.java.name, "no verification possible")
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    fun onLostPasswordButton(view: View) {
        deleteMessage()
        //authenticationViewModel.lostPassword(findViewById<TextView>(R.id.lpaEmail).text.toString())
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Log.i(this::class.java.name, "user: $user")
        val actionCodeSettings = ActionCodeSettings.newBuilder().apply {
            setAndroidPackageName(
                "com.pingwinek.jens.cookandbake",
                true,
                null)
            setHandleCodeInApp(true)
            setUrl("https://www.pingwinek.de/cookandbake")
        }.build()
        //val task = user?.sendEmailVerification(actionCodeSettings)
        val task = user?.let { auth.sendSignInLinkToEmail(it.email!!, actionCodeSettings) }
        if (task != null) {
            task.addOnSuccessListener {
                Log.i(this::class.java.name, "email sent")
            }.addOnFailureListener {
                Log.i(this::class.java.name, "exception: $it")
            }.addOnCanceledListener {
                Log.i(this::class.java.name, "sending email canceled")
            }
        } else {
            Log.i(this::class.java.name, "task was null")
        }
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.lpaMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.lpaMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }
}