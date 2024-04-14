package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
    // Values for UI components
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val myNewEmail = MutableLiveData<String>()
    val newEmail : LiveData<String> = myNewEmail
    fun onNewEmailChange(email: String) { myNewEmail.value = email }

    private val myPassword = MutableLiveData<String>()
    val password : LiveData<String> = myPassword
    fun onPasswordChange(password: String) { myPassword.value = password }

    private val myIsPrivacyApproved = MutableLiveData<Boolean>()
    val isPrivacyApproved : LiveData<Boolean> = myIsPrivacyApproved
    fun onIsPrivacyApprovedChange(approved: Boolean) { myIsPrivacyApproved.value = approved }

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

                FirebaseAuthService.ActionCodeOperation.CODE_INVALID -> {
                    authActionResult.postValue(AuthService.AuthActionResult.EXC_RESET_OR_VERIFY_CODE_INVALID)
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

    fun invalidateResult() {
        authActionResult.postValue(AuthService.AuthActionResult.NOTHING)
    }

//    fun register(email: String, password: String, dataPolicyChecked: Boolean) {
    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            var result = FirebaseAuthService.registerWithEmailAndPassword(
                newEmail.value ?: "",
                password.value ?: "",
                isPrivacyApproved.value ?: false
            )
            if (result == AuthService.AuthActionResult.REGISTRATION_SUCCEEDED) {
                result = FirebaseAuthService.sendVerificationEmail()
            }
            authActionResult.postValue(result)
        }
    }

//    fun resetPassword(password: String) {
    fun resetPassword() {
        if (oobCode.isNullOrEmpty()) {
            authActionResult.postValue(AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON)
            logError(Exception("action code is null or empty"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = FirebaseAuthService.resetPassword(
                password.value ?: "",
                oobCode!!
            )
            if (result == AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED) {
                changeAuthStatus(AuthService.AuthStatus.SIGNED_OUT)
                linkMode.postValue(EmailLinkMode.UNKNOWN)
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

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = FirebaseAuthService.signIn(
                newEmail.value ?: "",
                password.value ?: ""
            )
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