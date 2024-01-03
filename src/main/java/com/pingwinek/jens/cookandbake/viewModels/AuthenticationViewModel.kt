package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthenticationViewModel(application: Application) : AndroidViewModel(application), FirebaseAuth.AuthStateListener {

    class PingwinekAuthenticationException(message: String) : Exception(message) {

        override fun toString(): String {
            return "${this::class.java.name}: ${super.toString()}"
        }
    }

    enum class ResultType() {
        ACCOUNT_CREATED,
        VERIFICATION_EMAIL_SENT,
        EMAIL_VERIFIED,
        SIGNED_IN,
        PASSWORD_RESET_SENT,
        PASSWORD_RESET_CONFIRMED,
        SIGNED_OUT,
        ACCOUNT_DELETED,
        EXCEPTION
    }

    enum class EmailLinkMode {
        VERIFY,
        RESET,
        UNKNOWN
    }

    val auth = FirebaseAuth.getInstance()

    val result = MutableLiveData<ResultType>()
    val errorMessage = MutableLiveData<String>()
    val linkMode = MutableLiveData<EmailLinkMode>()
    val oobCode = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    private var emailFromIntent: String? = null

    init {
        auth.addAuthStateListener(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implement Authentication State Listener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (auth.currentUser == null) {
            result.postValue(ResultType.SIGNED_OUT)
            email.postValue("")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Authentication Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun checkActionCodeForIntent(intent: Intent) {
        if (intent.data == null) {
            Log.i(this::class.java.name, "no intent data")
            return
        }

        val actionCode = extractActionCode(intent)
        if (actionCode == null) {
            postError("no action code available")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val actionCodeResult = suspendedFunction(auth.checkActionCode(actionCode))
                when (actionCodeResult.operation) {
                    0 -> {
                        oobCode.postValue(actionCode!!)
                        actionCodeResult.info?.let {
                            emailFromIntent = it.email
                        }
                        linkMode.postValue(EmailLinkMode.RESET)
                    }

                    1 -> {
                        oobCode.postValue(actionCode!!)
                        actionCodeResult.info?.let {
                            emailFromIntent = it.email
                        }
                        linkMode.postValue(EmailLinkMode.VERIFY)
                    }

                    4 -> {
                        oobCode.postValue(actionCode!!)
                        actionCodeResult.info?.let {
                            emailFromIntent = it.email
                        }
                        linkMode.postValue(EmailLinkMode.VERIFY)
                    }

                    else -> {
                        linkMode.postValue(EmailLinkMode.UNKNOWN)
                        postError("unknown action code for operation ${actionCodeResult.operation}")
                    }
                }
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun deleteAccount() {
        if (auth.currentUser == null) {
            postError("no signed in user")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.currentUser!!.delete())
                result.postValue(ResultType.ACCOUNT_DELETED)
                email.postValue("")
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun isSignedIn(): Boolean = auth.currentUser != null

    fun isVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false

    fun isSignedInAndVerified(): Boolean = isSignedIn() && isVerified()

    fun register(email: String, password: String) {
        if (email.isEmpty()) {
            postError("email is empty string")
            return
        }

        if (password.isEmpty()) {
            postError("password is empty string")
            return
        }

        var registrationSuccessful = false
        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.createUserWithEmailAndPassword(email, password))
                result.postValue(ResultType.ACCOUNT_CREATED)
                registrationSuccessful = true
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
        Log.i(this::class.java.name, "registration: viewModelScope exited")
        if (registrationSuccessful) sendVerificationEmail()
    }

    fun resetPassword(password: String, actionCode: String) {
/*
        if (auth.currentUser == null) {
            postError("no signed in user available")
            return
        }
 */
        if (auth.currentUser != null && auth.currentUser!!.email != emailFromIntent) {
            postError("provided code not valid for signed in user")
            return
        }
        if (password.isEmpty()) {
            postError("email is empty string")
            return
        }
        if (actionCode.isEmpty()) {
            postError("action code is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.confirmPasswordReset(actionCode, password))
                result.postValue(ResultType.PASSWORD_RESET_CONFIRMED)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun retrieveEmail() {
        auth.currentUser?.let { user ->
            email.postValue(user.email)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            postError("email is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.sendPasswordResetEmail(email, getActionCodeSettings()))
                result.postValue(ResultType.PASSWORD_RESET_SENT)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun sendVerificationEmail() {
        if (auth.currentUser == null) {
            postError("no signed in user available")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                //suspendedFunction(auth.currentUser!!.sendEmailVerification(getActionCodeSettings()))
                suspendedFunction(auth.sendSignInLinkToEmail(auth.currentUser!!.email!!, getActionCodeSettings()))
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty()) {
            postError("email is empty string")
            return
        }

        if (password.isEmpty()) {
            postError("password is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.signInWithEmailAndPassword(email, password))
                result.postValue(ResultType.SIGNED_IN)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun verifyEmail(verificationLink: String) {
        if (auth.currentUser == null) {
            postError("no signed in user available")
            return
        }
        if (auth.currentUser!!.email != emailFromIntent) {
            postError("provided code not valid for signed in user")
            return
        }
        if (verificationLink.isEmpty()) {
            postError("verification link is empty string")
            return
        }
        if (!auth.isSignInWithEmailLink(verificationLink)) {
            postError("verification link is not valid")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credential = EmailAuthProvider.getCredentialWithLink(auth.currentUser!!.email!!, verificationLink)
                suspendedFunction(auth.currentUser!!.reauthenticate(credential))
                result.postValue(ResultType.EMAIL_VERIFIED)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Support Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun extractActionCode(intent: Intent): String? {
        return intent.data?.getQueryParameter("link")
            ?.let { innerUri ->
                Uri.parse(innerUri).getQueryParameter("oobCode")
            }
    }

    private fun getActionCodeSettings(): ActionCodeSettings {
        return ActionCodeSettings.newBuilder().apply {
            setAndroidPackageName(
                "com.pingwinek.jens.cookandbake",
                true,
                null)
            handleCodeInApp = true
            url = "https://www.pingwinek.de/cookandbake"
        }.build()
    }

    private fun postError(message: String) {
        errorMessage.postValue(message)
        result.postValue(ResultType.EXCEPTION)
    }

    private fun postError(exception: Exception) {
        postError(exception.toString())
    }

    private suspend fun <T> suspendedFunction(task: Task<T>): T {
        return suspendCoroutine { continuation ->
            task.addOnCompleteListener { resultingTask ->
                if (resultingTask.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Unknown exception - something went wrong"))
                }
            }
        }
    }
}