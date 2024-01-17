package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.pingwinek.jens.cookandbake.BuildConfig
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.firestore.SuspendedCoroutineWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TODO check email format, check password security - at registration
 */
class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    private class PingwinekAuthenticationException(private val exception: Exception) : Exception(exception) {

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
        UNKNOWN,
        VERIFIED
    }

    enum class AuthStatus {
        SIGNED_OUT,
        SIGNED_IN,
        VERIFIED
    }

    private val auth = FirebaseAuth.getInstance()
    //private val firebaseAuth = FirebaseAuthService.getInstance(application as PingwinekCooksApplication)

    val result = MutableLiveData<ResultType>()
    val linkMode = MutableLiveData<EmailLinkMode>()
    val email = MutableLiveData<String>()
    /*
    Call via changeStatus to make sure, that value is only updated, when status changes
     */
    val authStatus = MutableLiveData<AuthStatus>()

    var errorMessage: String = ""

    private var oobCode: String? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Authentication Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun checkActionCodeForIntent(intent: Intent) {
        if (intent.data == null) {
            return
        }

        //val actionCode = FirebaseAuthService.extractActionCode(intent.data!!) ?: return
        val actionCode = extractActionCode(intent) ?: return

        viewModelScope.launch(Dispatchers.IO) {
/*
            try {
                emailFromIntent = firebaseAuth.getEmailForResetActionCode(actionCode)
                if (emailFromIntent != null) {
                    oobCode = actionCode
                    linkMode.postValue(EmailLinkMode.RESET)
                } else {
                    linkMode.postValue(EmailLinkMode.UNKNOWN)
                }
            } catch (exception: Exception) {
                postError(getString(R.string.unknownException))
                logError(exception)
            }
*/
            try {
                val actionCodeResult = SuspendedCoroutineWrapper.suspendedFunction(auth.checkActionCode(actionCode))
                when (actionCodeResult.operation) {
                    0 -> {
                        oobCode = actionCode
                        actionCodeResult.info?.let {
                            email.postValue(it.email)
                        }
                        linkMode.postValue(EmailLinkMode.RESET)
                    }

                    1 -> {
                        SuspendedCoroutineWrapper.suspendedFunction(auth.applyActionCode(actionCode))
                        auth.currentUser?.let { user ->
                            SuspendedCoroutineWrapper.suspendedFunction(user.getIdToken(true))
                            SuspendedCoroutineWrapper.suspendedFunction(user.reload())
                        }
                        linkMode.postValue(EmailLinkMode.VERIFIED)
                    }

                    else -> {
                        linkMode.postValue(EmailLinkMode.UNKNOWN)
                    }
                }
            } catch (exception: Exception) {
                postError(getString(R.string.unknownException))
                logError(exception)
            }
        }
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            changeAuthStatus(AuthStatus.SIGNED_OUT)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.reload())
                if (auth.currentUser == null) {
                    changeAuthStatus(AuthStatus.SIGNED_OUT)
                } else if (auth.currentUser!!.isEmailVerified) {
                    changeAuthStatus(AuthStatus.VERIFIED)
                } else {
                    changeAuthStatus(AuthStatus.SIGNED_IN)
                }
            } catch (exception: Exception) {
                changeAuthStatus(AuthStatus.SIGNED_OUT)
            }
        }
    }

    fun deleteAccount() {
        if (auth.currentUser == null) {
            postError(getString(R.string.noSignedInUser))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.delete())
                result.postValue(ResultType.ACCOUNT_DELETED)
                changeAuthStatus(AuthStatus.SIGNED_OUT)
                email.postValue("")
            } catch (exception: Exception) {
                postError(getString(R.string.deleteFailed, exception.localizedMessage))
                logError(exception)
            }
        }
    }

    fun register(email: String, password: String, dataPolicyChecked: Boolean) {
        if (email.isEmpty()) {
            postError(getString(R.string.emailMalformatted))
            return
        }

        if (password.isEmpty()) {
            postError(getString(R.string.passwordMalformatted))
            return
        }

        if (!dataPolicyChecked) {
            postError(getString(R.string.dataProtectionNotChecked))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.createUserWithEmailAndPassword(email, password))
                result.postValue(ResultType.ACCOUNT_CREATED)
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.sendEmailVerification(getActionCodeSettings(true)))
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
                changeAuthStatus(AuthStatus.SIGNED_IN)
            } catch (exception: Exception) {
                postError(getString(R.string.registrationFailed, exception.localizedMessage))
                logError(exception)
            }
        }
    }

    fun resetPassword(password: String) {
        /*
        if (auth.currentUser != null && auth.currentUser!!.email != emailFromIntent) {
            postError(getString(R.string.resetForWrongEmail))
            return
        }

         */
        if (password.isEmpty()) {
            postError(getString(R.string.passwordMalformatted))
            return
        }
        if (oobCode.isNullOrEmpty()) {
            postError(getString(R.string.unknownException))
            logError(Exception("action code is null or empty"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.confirmPasswordReset(oobCode!!, password))
                result.postValue(ResultType.PASSWORD_RESET_CONFIRMED)
                changeAuthStatus(AuthStatus.SIGNED_OUT)
            } catch (exception: Exception) {
                postError(getString(R.string.resetFailed, exception.localizedMessage))
                logError(exception)
            } finally {
                oobCode = null
            }
        }
    }

    fun retrieveEmail() {
        if (auth.currentUser == null) {
            email.postValue("")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    auth.currentUser!!.reload()
                    auth.currentUser?.let { user ->
                        email.postValue(user.email)
                    }
                } catch (exception: Exception) {
                    email.postValue("")
                    postError(getString(R.string.retrieveEmailFailed, exception.localizedMessage))
                    logError(exception)
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            postError(getString(R.string.emailMalformatted))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    auth.sendPasswordResetEmail(
                        email,
                        getActionCodeSettings(true)))
                result.postValue(ResultType.PASSWORD_RESET_SENT)
            } catch (exception: Exception) {
                postError(getString(R.string.sendResetFailed, exception.localizedMessage))
                logError(exception)
            }
        }
    }

    fun sendVerificationEmail() {
        if (auth.currentUser == null) {
            postError(getString(R.string.noSignedInUser))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    auth.currentUser!!.sendEmailVerification(
                        getActionCodeSettings(true)))
                result.postValue(ResultType.VERIFICATION_EMAIL_SENT)
            } catch (exception: Exception) {
                postError(getString(R.string.sendVerificationEmail, exception.localizedMessage))
                logError(exception)
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty()) {
            postError(getString(R.string.emailMalformatted))
            return
        }

        if (password.isEmpty()) {
            postError(getString(R.string.passwordMalformatted))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.signInWithEmailAndPassword(email, password))
                result.postValue(ResultType.SIGNED_IN)

                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.reload())

                if (auth.currentUser == null) {
                    changeAuthStatus(AuthStatus.SIGNED_OUT)
                } else if (auth.currentUser!!.isEmailVerified) {
                    changeAuthStatus(AuthStatus.VERIFIED)
                } else {
                    changeAuthStatus(AuthStatus.SIGNED_IN)
                }
            } catch (exception: Exception) {
                postError(getString(R.string.loginFailed, exception.localizedMessage))
                logError(exception)
            }
        }
    }

    fun signOut() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                auth.signOut()
                if (auth.currentUser == null) {
                    result.postValue(ResultType.SIGNED_OUT)
                    changeAuthStatus(AuthStatus.SIGNED_OUT)
                    email.postValue("")
                }
            }
        } catch (exception: Exception) {
            postError(getString(R.string.logoutFailed, exception.localizedMessage))
            logError(exception)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Support Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun changeAuthStatus(newAuthStatus: AuthStatus) {
        if (newAuthStatus != authStatus.value) {
            authStatus.postValue(newAuthStatus)
        }
    }

    private fun extractActionCode(intent: Intent): String? {
        if (intent.data == null) return null

        val link = intent.data!!.getQueryParameter("link")?.let {
            Uri.parse(it)
        } ?: intent.data!!

        return link.getQueryParameter("oobCode")
    }

    /**
     * Provides the [ActionCodeSettings] for verification and reset password emails. Uses
     * [BuildConfig.APPLICATION_ID] from the gradle build configuration as package name. The
     * redirect URL for redirecting to the app after verification is configured in urls.xml.
     *
     * @param handleInApp false for verification email and true for reset password email
     *
     * The verification email cannot be handled inside the app due to missing verification method
     * in the Firebase Auth api. The reset password email needs to be handled in the app, where
     * we can set a new password.
     */
    private fun getActionCodeSettings(handleInApp: Boolean): ActionCodeSettings {
        return ActionCodeSettings.newBuilder().apply {
            setAndroidPackageName(
                BuildConfig.APPLICATION_ID,
                true,
                null)
            handleCodeInApp = handleInApp
            url = getApplication<Application>().getString(R.string.URL_VERIFY_REDIRECT)
        }.build()
    }

    private fun getString(id: Int): String {
        return getApplication<Application>().getString(id)
    }

    private fun getString(id: Int, placeholder: String?): String {
        return getApplication<Application>().getString(id, placeholder)
    }

    private fun logError(exception: Exception) {
        Log.e(this::class.java.name, exception.toString())
    }

    private fun postError(message: String) {
        postError(PingwinekAuthenticationException(message))
    }

    private fun postError(exception: Exception) {
        errorMessage = PingwinekAuthenticationException(exception).toString()
        result.postValue(ResultType.EXCEPTION)
    }
}