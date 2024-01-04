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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthenticationViewModel(application: Application) : AndroidViewModel(application), FirebaseAuth.AuthStateListener {

    class PingwinekAuthenticationException(private val exception: Exception) : Exception(exception) {

        constructor(message: String): this(Exception(message))

        override fun toString(): String {
            return if (exception is PingwinekAuthenticationException) {
                exception.toString()
            } else {
                "${this::class.java.name}: ${super.toString()}"
            }
        }
    }

    enum class ResultType() {
        ACCOUNT_CREATED,
        VERIFICATION_EMAIL_SENT,
        SIGNED_IN,
        PASSWORD_RESET_SENT,
        PASSWORD_RESET_CONFIRMED,
        SIGNED_OUT,
        ACCOUNT_DELETED,
        EXCEPTION
    }

    enum class EmailLinkMode {
        RESET,
        UNKNOWN
    }

    private val auth = FirebaseAuth.getInstance()

    val result = MutableLiveData<ResultType>()
    var errorMessage: String = ""
    val linkMode = MutableLiveData<EmailLinkMode>()
    val email = MutableLiveData<String>()

    private var emailFromIntent: String? = null
    private var oobCode: String = ""

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
                        oobCode = actionCode
                        actionCodeResult.info?.let {
                            emailFromIntent = it.email
                        }
                        linkMode.postValue(EmailLinkMode.RESET)
                    }
/*
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
*/
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
                postError(exception)
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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.createUserWithEmailAndPassword(email, password))
                result.postValue(ResultType.ACCOUNT_CREATED)
                suspendedFunction(auth.currentUser!!.sendEmailVerification(getActionCodeSettings(false)))
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
            } catch (exception: Exception) {
                postError(exception)
            }
        }
    }

    fun resetPassword(password: String) {
        if (auth.currentUser != null && auth.currentUser!!.email != emailFromIntent) {
            postError("provided code not valid for signed in user")
            return
        }
        if (password.isEmpty()) {
            postError("email is empty string")
            return
        }
        if (oobCode.isEmpty()) {
            postError("action code is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                suspendedFunction(auth.confirmPasswordReset(oobCode, password))
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
                suspendedFunction(
                    auth.sendPasswordResetEmail(
                        email,
                        getActionCodeSettings(true)))
                result.postValue(ResultType.PASSWORD_RESET_SENT)
            } catch (exception: Exception) {
                postError(exception)
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
                suspendedFunction(
                    auth.currentUser!!.sendEmailVerification(
                        getActionCodeSettings(false)))
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
            } catch (exception: Exception) {
                postError(exception)
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
                postError(exception)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

/*
    fun verifyEmail(password: String, intent: Intent) {
        if (auth.currentUser == null) {
            postError("no signed in user available")
            return
        }
        if (password.isEmpty()) {
            postError("verification link is empty string")
            return
        }
        if (!auth.isSignInWithEmailLink(intent.data.toString())) {
            postError("verification link is not valid")
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credentialWithLink = EmailAuthProvider.getCredentialWithLink(auth.currentUser!!.email!!, intent.data.toString())
                suspendedFunction(auth.currentUser!!.reauthenticate(credentialWithLink))
                Log.i(this::class.java.name, "isVerified: ${isVerified()}")
                val credential = EmailAuthProvider.getCredential(auth.currentUser!!.email!!, password)
                suspendedFunction(auth.currentUser!!.reauthenticate(credential))
                Log.i(this::class.java.name, "isVerified: ${isVerified()}")
                result.postValue(ResultType.EMAIL_VERIFIED)
            } catch (exception: Exception) {
                postError(exception.toString())
            }
        }
    }
*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Support Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun extractActionCode(intent: Intent): String? {
        return intent.data?.getQueryParameter("link")
            ?.let { innerUri ->
                Uri.parse(innerUri).getQueryParameter("oobCode")
            }
    }

    private fun getActionCodeSettings(handleInApp: Boolean): ActionCodeSettings {
        return ActionCodeSettings.newBuilder().apply {
            setAndroidPackageName(
                "com.pingwinek.jens.cookandbake",
                true,
                null)
            handleCodeInApp = handleInApp
            url = "https://pingwinekcooks.firebaseapp.com"
        }.build()
    }

    private fun postError(message: String) {
        postError(PingwinekAuthenticationException(message))
    }

    private fun postError(exception: Exception) {
        errorMessage = PingwinekAuthenticationException(exception).toString()
        result.postValue(ResultType.EXCEPTION)
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