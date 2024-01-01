package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationViewModel(application: Application) : AndroidViewModel(application), FirebaseAuth.AuthStateListener {

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

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (auth.currentUser == null) {
            result.postValue(ResultType.SIGNED_OUT)
            email.postValue("")
        }
    }

    fun retrieveEmail() {
        auth.currentUser?.let { user ->
            email.postValue(user.email)
        }
    }

    fun isSignedIn(): Boolean = auth.currentUser != null

    fun isVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false

    fun isSignedInAndVerified(): Boolean = isSignedIn() && isVerified()

    fun checkActionCodeForIntent(intent: Intent) {
        val actionCode = extractActionCode(intent)
        if (actionCode == null) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("no action code available")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<ActionCodeResult>> { continuation ->
                auth.checkActionCode(actionCode).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }
            if (task.isSuccessful) {
                val actionCodeResult = task.result
                when (actionCodeResult.operation) {
                    0 -> {
                        linkMode.postValue(EmailLinkMode.RESET)
                        oobCode.postValue(actionCode!!)
                        actionCodeResult.info?.let {
                            email.postValue(it.email)
                        }
                    }

                    1 -> {
                        linkMode.postValue(EmailLinkMode.VERIFY)
                        oobCode.postValue(actionCode!!)
                        actionCodeResult.info?.let {
                            email.postValue(it.email)
                        }
                    }

                    else -> {
                        result.postValue(ResultType.EXCEPTION)
                        errorMessage.postValue("unknown action code")
                        linkMode.postValue(EmailLinkMode.UNKNOWN)
                    }
                }
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun deleteAccount() {
        if (auth.currentUser == null) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("no signed in user")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<Void>> { continuation ->
                auth.currentUser!!.delete().addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }
            if (task.isSuccessful) {
                result.postValue(ResultType.ACCOUNT_DELETED)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun register(email: String, password: String) {
        if (email.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("email is empty string")
            return
        }

        if (password.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("password is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<AuthResult>> { continuation ->
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }


            if (task.isSuccessful) {
                result.postValue(ResultType.ACCOUNT_CREATED)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun resetPassword(password: String, actionCode: String) {
        if (auth.currentUser == null) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("no signed in user available")
            return
        }
        if (auth.currentUser!!.email != email.value) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("provided code not valid for signed in user")
            return
        }
        if (password.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("email is empty string")
            return
        }
        if (actionCode.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("action code is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<Void>> { continuation ->
                auth.confirmPasswordReset(actionCode, password).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }
            if (task.isSuccessful) {
                result.postValue(ResultType.PASSWORD_RESET_CONFIRMED)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("email is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<Void>> { continuation ->
                auth.sendPasswordResetEmail(email, getActionCodeSettings()).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }

            if (task.isSuccessful) {
                result.postValue(ResultType.PASSWORD_RESET_SENT)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun sendVerificationEmail() {
        if (auth.currentUser == null) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("no signed in user available")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<Void>> { continuation ->
                auth.currentUser!!.sendEmailVerification(getActionCodeSettings()).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }

            if (task.isSuccessful) {
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("email is empty string")
            return
        }

        if (password.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("password is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<AuthResult>> { continuation ->
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }


            if (task.isSuccessful) {
                result.postValue(ResultType.SIGNED_IN)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun verifyEmail(actionCode: String) {
        if (auth.currentUser == null) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("no signed in user available")
            return
        }
        if (auth.currentUser!!.email != email.value) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("provided code not valid for signed in user")
            return
        }
        if (actionCode.isEmpty()) {
            result.postValue(ResultType.EXCEPTION)
            errorMessage.postValue("action code is empty string")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val task = suspendCoroutine<Task<Void>> { continuation ->
                val credential = EmailAuthProvider.getCredentialWithLink(auth.currentUser!!.email!!, actionCode)
                auth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                    continuation.resume(task)
                }
            }
            if (task.isSuccessful) {
                result.postValue(ResultType.EMAIL_VERIFIED)
            } else {
                result.postValue(ResultType.EXCEPTION)
                errorMessage.postValue(task.exception.toString())
            }
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

    private fun extractActionCode(intent: Intent): String? {
        return intent.data?.getQueryParameter("link")
            ?.let { innerUri ->
                Uri.parse(innerUri).getQueryParameter("oobCode")
            }
    }
}