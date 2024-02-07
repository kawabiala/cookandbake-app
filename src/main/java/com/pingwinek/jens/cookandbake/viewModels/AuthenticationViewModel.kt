package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.lib.firestore.FirebaseAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 */
class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    enum class EmailLinkMode {
        RESET,
        UNKNOWN,
        VERIFIED
    }

    /*
    Call via changeStatus to make sure, that value is only updated, when status changes
     */
    val authStatus = MutableLiveData<AuthService.AuthStatus>()

    val authActionResult = MutableLiveData<AuthService.AuthActionResult>()
    val linkMode = MutableLiveData<EmailLinkMode>()
    val email = MutableLiveData<String>()

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
                    if (FirebaseAuthService.verify(actionCode) == AuthService.AuthActionResult.VERIFICATION_SUCCEEDED) {
                        linkMode.postValue(EmailLinkMode.VERIFIED)
                        changeAuthStatus(AuthService.AuthStatus.VERIFIED)
                        authActionResult.postValue(AuthService.AuthActionResult.VERIFICATION_SUCCEEDED)
                    } else {
                        linkMode.postValue(EmailLinkMode.UNKNOWN)
                        authActionResult.postValue(AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON)
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
            changeAuthStatus(FirebaseAuthService.checkAuthStatus())
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = FirebaseAuthService.deleteAccount()
            if (result == AuthService.AuthActionResult.DELETE_SUCCEEDED) {
                changeAuthStatus(AuthService.AuthStatus.SIGNED_OUT)
                email.postValue("")
            }
            authActionResult.postValue(result)
        }
    }

    fun register(email: String, password: String, dataPolicyChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            var result = FirebaseAuthService.registerWithEmailAndPassword(email, password, dataPolicyChecked)
            if (result == AuthService.AuthActionResult.REGISTRATION_SUCCEEDED) {
                result = FirebaseAuthService.sendVerificationEmail()
            }
            authActionResult.postValue(result)
        }
    }

    fun resetPassword(password: String) {
        if (oobCode.isNullOrEmpty()) {
            authActionResult.postValue(AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON)
            logError(Exception("action code is null or empty"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = FirebaseAuthService.resetPassword(password, oobCode!!)
            if (result == AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED) {
                changeAuthStatus(AuthService.AuthStatus.SIGNED_OUT)
            }
            authActionResult.postValue(result)
        }
    }

    fun retrieveEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            email.postValue(FirebaseAuthService.getEmail())
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authActionResult.postValue(FirebaseAuthService.sendPasswordResetEmail(email))
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            authActionResult.postValue(FirebaseAuthService.sendVerificationEmail())
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = FirebaseAuthService.signIn(email, password)
            if (result == AuthService.AuthActionResult.SIGNIN_SUCCEEDED) {
                changeAuthStatus(FirebaseAuthService.checkAuthStatus())
            } else {
                authActionResult.postValue(result)
            }
        }
    }

    fun signOut() {
        val result = FirebaseAuthService.signOut()
        if (result == AuthService.AuthActionResult.SIGNOUT_SUCCEEDED) {
            changeAuthStatus(AuthService.AuthStatus.SIGNED_OUT)
            email.postValue("")
        }
        authActionResult.postValue(result)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Support Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun changeAuthStatus(newAuthStatus: AuthService.AuthStatus) {
        if (newAuthStatus != authStatus.value) {
            authStatus.postValue(newAuthStatus)
        }
    }

    private fun logError(exception: Exception) {
        Log.e(this::class.java.name, exception.toString())
    }

}