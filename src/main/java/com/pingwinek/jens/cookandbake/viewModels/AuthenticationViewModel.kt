package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.firestore.FirebaseAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
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
        val actionCode = FirebaseAuthService.extractActionCode(intent) ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val checkActionCodeResult = FirebaseAuthService.getActionCodeResult(actionCode)
            when (checkActionCodeResult.operation) {
                FirebaseAuthService.ActionCodeOperation.RESET -> {
                    oobCode = actionCode
                    email.postValue(checkActionCodeResult.email!!)
                    linkMode.postValue(EmailLinkMode.RESET)
                }

                FirebaseAuthService.ActionCodeOperation.VERIFY -> {
                    if (FirebaseAuthService.verify(actionCode) == FirebaseAuthService.AuthActionResult.VERIFICATION_SUCCEEDED) {
                        linkMode.postValue(EmailLinkMode.VERIFIED)
                    } else {
                        linkMode.postValue(EmailLinkMode.UNKNOWN)
                    }
                }

                else -> {
                    linkMode.postValue(EmailLinkMode.UNKNOWN)
                }
            }
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.checkAuthStatus()) {
                FirebaseAuthService.AuthStatus.VERIFIED -> { changeAuthStatus(AuthStatus.VERIFIED) }
                FirebaseAuthService.AuthStatus.SIGNED_IN -> { changeAuthStatus(AuthStatus.SIGNED_IN) }
                else -> { changeAuthStatus(AuthStatus.SIGNED_OUT)}
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.deleteAccount()) {
                FirebaseAuthService.AuthActionResult.DELETE_SUCCEEDED -> {
                    result.postValue(ResultType.ACCOUNT_DELETED)
                    changeAuthStatus(AuthStatus.SIGNED_OUT)
                    email.postValue("")
                }

                FirebaseAuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> {
                    postError(getString(R.string.noSignedInUser))
                }

                else -> {
                    postError(getString(R.string.deleteFailed))
                }
            }
        }
    }

    fun register(email: String, password: String, dataPolicyChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.register(email, password, dataPolicyChecked)) {
                FirebaseAuthService.AuthActionResult.REGISTRATION_SUCCEEDED -> {
                    when (FirebaseAuthService.sendVerificationEmail()) {
                        FirebaseAuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> { result.postValue(ResultType.VERIFICATION_EMAIL_SENT) }
                        else -> { postError(getString(R.string.registrationFailed)) }
                    }
                }
                FirebaseAuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> { postError(getString(R.string.emailMalformatted)) }
                FirebaseAuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> { postError(getString(R.string.passwordMalformatted)) }
                FirebaseAuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED -> { postError(getString(R.string.dataProtectionNotChecked)) }
                else -> { postError(getString(R.string.registrationFailed)) }
            }
        }
    }

    fun resetPassword(password: String) {
        if (oobCode.isNullOrEmpty()) {
            postError(getString(R.string.unknownException))
            logError(Exception("action code is null or empty"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.resetPassword(password, oobCode!!)) {
                FirebaseAuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> {
                    result.postValue(ResultType.PASSWORD_RESET_CONFIRMED)
                    changeAuthStatus(AuthStatus.SIGNED_OUT)
                }
                FirebaseAuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> { postError(getString(R.string.passwordMalformatted)) }
                else -> { postError(getString(R.string.resetFailed)) }
            }
        }
    }

    fun retrieveEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            email.postValue(FirebaseAuthService.getEmail())
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.sendPasswordResetEmail(email)) {
                FirebaseAuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED -> { result.postValue(ResultType.PASSWORD_RESET_SENT) }
                FirebaseAuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> { postError(getString(R.string.emailMalformatted)) }
                else -> { postError(getString(R.string.sendResetFailed)) }
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.sendVerificationEmail()) {
                FirebaseAuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> { result.postValue(ResultType.VERIFICATION_EMAIL_SENT) }
                FirebaseAuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> { postError(getString(R.string.noSignedInUser)) }
                else -> { postError(getString(R.string.sendVerificationFailed))}
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (FirebaseAuthService.signIn(email, password)) {
                FirebaseAuthService.AuthActionResult.SIGNIN_SUCCEEDED -> {
                    result.postValue(ResultType.SIGNED_IN)
                    when (FirebaseAuthService.checkAuthStatus()) {
                        FirebaseAuthService.AuthStatus.VERIFIED -> { changeAuthStatus(AuthStatus.VERIFIED) }
                        FirebaseAuthService.AuthStatus.SIGNED_IN -> { changeAuthStatus(AuthStatus.SIGNED_IN) }
                        else -> { changeAuthStatus(AuthStatus.SIGNED_OUT) }
                    }
                }
                FirebaseAuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> { postError(getString(R.string.emailMalformatted)) }
                FirebaseAuthService.AuthActionResult.EXC_PASSWORD_EMPTY -> { postError(getString(R.string.passwordMalformatted)) }
                else -> { postError(getString(R.string.loginFailed)) }
            }
        }
    }

    fun signOut() {
        when (FirebaseAuthService.signOut()) {
            FirebaseAuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> {
                result.postValue(ResultType.SIGNED_OUT)
                changeAuthStatus(AuthStatus.SIGNED_OUT)
                email.postValue("")
            }
            else -> { postError(getString(R.string.logoutFailed)) }
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

    private fun getString(id: Int): String {
        return getApplication<Application>().getString(id)
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