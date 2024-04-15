package com.pingwinek.jens.cookandbake.lib.firestore

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.pingwinek.jens.cookandbake.BuildConfig
import com.pingwinek.jens.cookandbake.lib.AuthService
import java.util.LinkedList

/**
 * TODO Log errors
 */
class FirebaseAuthService {
    enum class ActionCodeOperation {
        RESET,
        VERIFY,
        UNKNOWN,
        CODE_INVALID
    }

    data class CheckActionCodeResult(
        val isSuccess: Boolean,
        val operation: ActionCodeOperation,
        val email: String?
    )

    ////////////////////////////////////////////////////////////////////////////////////////////////

    companion object : AuthService {

        private val auth = FirebaseAuth.getInstance()
        private val authListeners = LinkedList<AuthService.AuthenticationListener>()

        private const val FB_AUTH_DOMAIN = "https://www.pingwinek.de"
        private const val QP_LINK = "link"
        private const val QP_OOBCODE = "oobCode"
        private const val HANDLE_IN_APP = true
        private const val TIMEOUT: Long = 10000

        suspend fun checkAuthStatus(): AuthService.AuthStatus {
            if (auth.currentUser == null) {
                return AuthService.AuthStatus.SIGNED_OUT
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.currentUser!!.reload())
                if (auth.currentUser == null) {
                    AuthService.AuthStatus.SIGNED_OUT
                } else if (auth.currentUser!!.isEmailVerified) {
                    AuthService.AuthStatus.VERIFIED
                } else {
                    AuthService.AuthStatus.SIGNED_IN
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthStatus.UNKNOWN
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthStatus.UNKNOWN
            }
        }

        suspend fun deleteAccount(): AuthService.AuthActionResult {
            if (auth.currentUser == null) {
                return AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(TIMEOUT, auth.currentUser!!.delete())
                AuthService.AuthActionResult.DELETE_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                if (exception.getUnderlyingException() is FirebaseAuthRecentLoginRequiredException) {
                    AuthService.AuthActionResult.EXC_DELETE_FAILED_RECENT_LOGIN_REQUIRED
                } else {
                    AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON
                }
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON
            }
        }

        /**
         * If isSuccess true, then email is guaranteed to be not null
         */
        suspend fun getActionCodeResult(actionCode: String): CheckActionCodeResult {
            return try {
                val actionCodeResult = SuspendedCoroutineWrapper.suspendedFunction(auth.checkActionCode(actionCode))
                when (actionCodeResult.operation) {
                    0 -> {
                        actionCodeResult.info?.let { info ->
                            CheckActionCodeResult(true, ActionCodeOperation.RESET, info.email)
                        } ?: CheckActionCodeResult(false, ActionCodeOperation.UNKNOWN, null)
                    }
                    1 -> {
                        actionCodeResult.info?.let { info ->
                            CheckActionCodeResult(true, ActionCodeOperation.VERIFY, info.email)
                        } ?: CheckActionCodeResult(false, ActionCodeOperation.UNKNOWN, null)
                    }
                    else -> {
                        CheckActionCodeResult(false, ActionCodeOperation.UNKNOWN, null)
                    }
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                if (exception.getUnderlyingException() is FirebaseAuthActionCodeException) {
                    CheckActionCodeResult(false, ActionCodeOperation.CODE_INVALID, null)
                } else {
                    CheckActionCodeResult(false, ActionCodeOperation.UNKNOWN, null)
                }
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                CheckActionCodeResult(false, ActionCodeOperation.UNKNOWN, null)
            }
        }

        suspend fun getEmail(): String {
            if (auth.currentUser == null) return ""
            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.currentUser!!.reload())
                auth.currentUser?.email ?: ""
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                ""
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                ""
            }
        }

        private fun notifyOnLogin() {
            authListeners.forEach { listener ->
                listener.onLogin()
            }
        }

        private fun notifyOnLogout() {
            authListeners.forEach { listener ->
                listener.onLogout()
            }
        }

        suspend fun registerWithEmailAndPassword(email: String, password: String, dataPolicyChecked: Boolean): AuthService.AuthActionResult {
            when {
                !isEmail(email) -> return AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
                !passesPasswordPolicy(password) -> return AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED
                !dataPolicyChecked -> return AuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.createUserWithEmailAndPassword(email, password)
                )
                AuthService.AuthActionResult.REGISTRATION_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON
            }
        }

        fun registerAuthenticationListener(authenticationListener: AuthService.AuthenticationListener) {
            authListeners.add(authenticationListener)
        }

        suspend fun resetPassword(password: String, oobCode: String): AuthService.AuthActionResult {
            if (!AuthService.PasswordPolicy.matches(password)) {
                return AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED
            }
            if (oobCode.isEmpty()) {
                return AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.confirmPasswordReset(oobCode, password))
                AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON
            }
        }

        suspend fun sendPasswordResetEmail(email: String): AuthService.AuthActionResult {
            if (!isEmail(email)) {
                return AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.sendPasswordResetEmail(
                        email,
                        getActionCodeSettings()))
                AuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON
            }
        }

        suspend fun sendVerificationEmail(): AuthService.AuthActionResult {
            if (auth.currentUser == null) {
                return AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.currentUser!!.sendEmailVerification(getActionCodeSettings()))
                AuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                return AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                return AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON
            }
        }

        suspend fun signIn(email: String, password: String): AuthService.AuthActionResult {
            when {
                !isEmail(email) -> return AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
                password.isEmpty() -> return AuthService.AuthActionResult.EXC_PASSWORD_EMPTY
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.signInWithEmailAndPassword(email, password))
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.currentUser!!.reload())

                if (auth.currentUser == null) {
                    AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
                } else {
                    AuthService.AuthActionResult.SIGNIN_SUCCEEDED
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                return AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                return AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
            }
        }

        fun signOut(): AuthService.AuthActionResult {
            auth.signOut()
            return if (auth.currentUser == null) {
                AuthService.AuthActionResult.SIGNOUT_SUCCEEDED
            } else {
                AuthService.AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON
            }
        }

        suspend fun verify(actionCode: String): AuthService.AuthActionResult {
            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.applyActionCode(actionCode))
                auth.currentUser?.let { user ->
                    SuspendedCoroutineWrapper.suspendedFunction(
                        TIMEOUT,
                        user.getIdToken(true))
                    SuspendedCoroutineWrapper.suspendedFunction(
                        TIMEOUT,
                        user.reload())
                    AuthService.AuthActionResult.VERIFICATION_SUCCEEDED
                } ?: AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                Log.e(this::class.java.name, exception.toString())
                AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Private Functions
        ////////////////////////////////////////////////////////////////////////////////////////////

        fun extractActionCode(intent: Intent): String? {
            if (intent.data == null) return null

            val link = intent.data!!.getQueryParameter(QP_LINK)?.let {
                Uri.parse(it)
            } ?: intent.data!!

            return link.getQueryParameter(QP_OOBCODE)
        }

        /**
         * Provides the [ActionCodeSettings] for verification and reset password emails. Uses
         * [BuildConfig.APPLICATION_ID] from the gradle build configuration as package name. The
         * redirect URL for redirecting to the app after verification is configured in urls.xml.
         */
        private fun getActionCodeSettings(): ActionCodeSettings {
            return ActionCodeSettings.newBuilder().apply {
                setAndroidPackageName(
                    BuildConfig.APPLICATION_ID,
                    true,
                    null)
                handleCodeInApp = HANDLE_IN_APP
                url = FB_AUTH_DOMAIN
            }.build()
        }

        private fun isEmail(email: String): Boolean {
            return (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        private fun passesPasswordPolicy(password: String): Boolean {
            return AuthService.PasswordPolicy.matches(password)
        }
    }

}