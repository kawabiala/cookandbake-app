package com.pingwinek.jens.cookandbake.lib.firestore

import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.pingwinek.jens.cookandbake.BuildConfig
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class FirebaseAuthService private constructor(private val application: PingwinekCooksApplication) {

    interface AuthenticationListener {
        fun onLogin()
        fun onLogout()
    }

    private val authListeners = LinkedList<AuthenticationListener>()

    fun registerAuthenticationListener(authenticationListener: AuthenticationListener) {
        authListeners.add(authenticationListener)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val auth = FirebaseAuth.getInstance()

    @Throws(SuspendedCoroutineWrapper.SuspendedCoroutineException::class)
    suspend fun getEmailForResetActionCode(actionCode: String): String? {
        val actionCodeResult = SuspendedCoroutineWrapper.suspendedFunction(auth.checkActionCode(actionCode))

        return if (actionCodeResult.operation == 0) {
            actionCodeResult.info?.email
        } else {
            null
        }
    }


    companion object : SingletonHolder<FirebaseAuthService, PingwinekCooksApplication>(::FirebaseAuthService) {

        private val auth = FirebaseAuth.getInstance()

        private const val FB_AUTH_DOMAIN = "https://www.pingwinek.de"
        private const val QP_LINK = "link"
        private const val QP_OOBCODE = "oobCode"
        private const val HANDLE_IN_APP = true
        private const val TIMEOUT: Long = 10000

        suspend fun checkAuthStatus(): AuthStatus {
            if (auth.currentUser == null) {
                return AuthStatus.SIGNED_OUT
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.reload())
                if (auth.currentUser == null) {
                    AuthStatus.SIGNED_OUT
                } else if (auth.currentUser!!.isEmailVerified) {
                    AuthStatus.VERIFIED
                } else {
                    AuthStatus.SIGNED_IN
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthStatus.UNKNOWN
            } catch (exception: Exception) {
                AuthStatus.UNKNOWN
            }
        }

        suspend fun deleteAccount(): AuthActionResult {
            if (auth.currentUser == null) {
                return AuthActionResult.EXC_NO_SIGNEDIN_USER
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(TIMEOUT, auth.currentUser!!.delete())
                AuthActionResult.DELETE_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON
            }
        }

        suspend fun register(email: String, password: String, dataPolicyChecked: Boolean): AuthActionResult {
            when {
                !isEmail(email) -> return AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
                !passesPasswordPolicy(password) -> return AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED
                !dataPolicyChecked -> return AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    TIMEOUT,
                    auth.createUserWithEmailAndPassword(email, password)
                )
                AuthActionResult.REGISTRATION_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON
            }
        }

        suspend fun resetPassword(password: String, oobCode: String): AuthActionResult {
            if (!PasswordPolicy.matches(password)) {
                return AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED
            }
            if (oobCode.isEmpty()) {
                return AuthActionResult.EXC_NO_OOBCOD_PROVIDED
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.confirmPasswordReset(oobCode, password))
                AuthActionResult.RESET_PASSWORD_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON
            }
        }

        suspend fun sendPasswordResetEmail(email: String): AuthActionResult {
            if (!isEmail(email)) {
                return AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(
                    auth.sendPasswordResetEmail(
                        email,
                        getActionCodeSettings()))
                AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON
            } catch (exception: Exception) {
                AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON
            }
        }

        suspend fun sendVerificationEmail(): AuthActionResult {
            if (auth.currentUser == null) {
                return AuthActionResult.EXC_NO_SIGNEDIN_USER
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.sendEmailVerification(getActionCodeSettings()))
                AuthActionResult.VERIFICATION_SEND_SUCCEEDED
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                return AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                return AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON
            }
        }

        suspend fun signIn(email: String, password: String): AuthActionResult {
            when {
                !isEmail(email) -> return AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED
                password.isEmpty() -> return AuthActionResult.EXC_PASSWORD_EMPTY
            }

            return try {
                SuspendedCoroutineWrapper.suspendedFunction(auth.signInWithEmailAndPassword(email, password))
                SuspendedCoroutineWrapper.suspendedFunction(auth.currentUser!!.reload())

                if (auth.currentUser == null) {
                    AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
                } else {
                    AuthActionResult.SIGNIN_SUCCEEDED
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                return AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                return AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON
            }
        }

        suspend fun signOut(): AuthActionResult {
            return try {
                auth.signOut()
                if (auth.currentUser == null) {
                    AuthActionResult.SIGNOUT_SUCCEEDED
                } else {
                    AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON
                }
            } catch (exception: SuspendedCoroutineWrapper.SuspendedCoroutineException) {
                AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON
            } catch ( exception: Exception) {
                AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Private Functions
        ////////////////////////////////////////////////////////////////////////////////////////////

        private fun extractActionCode(intent: Intent): String? {
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
            return PasswordPolicy.matches(password)
        }
    }

    enum class AuthActionResult {
        DELETE_SUCCEEDED,
        EXC_DATAPOLICY_NOT_ACCEPTED,
        EXC_DELETE_FAILED_WITHOUT_REASON,
        EXC_EMAIL_EMPTY_OR_MALFORMATTED,
        EXC_NO_OOBCOD_PROVIDED,
        EXC_NO_SIGNEDIN_USER,
        EXC_PASSWORD_EMPTY,
        EXC_PASSWORD_POLICY_CHECK_NOT_PASSED,
        EXC_REGISTRATION_FAILED_WITHOUT_REASON,
        EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON,
        EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON,
        EXC_SIGNIN_FAILED_WITHOUT_REASON,
        EXC_SIGNOUT_FAILED_WITHOUT_REASON,
        EXC_USER_ALREADY_EXISTS,
        EXC_VERIFICATION_FAILED_WITHOUT_REASON,
        EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON,
        REGISTRATION_SUCCEEDED,
        RESET_PASSWORD_SUCCEEDED,
        RESET_PASSWORD_SEND_SUCCEEDED,
        SIGNIN_SUCCEEDED,
        SIGNOUT_SUCCEEDED,
        VERIFICATION_SUCCEEDED,
        VERIFICATION_SEND_SUCCEEDED,
    }

    enum class AuthStatus {
        SIGNED_OUT,
        SIGNED_IN,
        VERIFIED,
        UNKNOWN
    }

    class PasswordPolicy {

        companion object {

            private val minLength = 8

            fun matches(password: String): Boolean {
                val result = runCatching {
                    require(password.length >= minLength)
                    require(password.none() { it.isWhitespace() } )
                    require(password.any() { it.isUpperCase() } )
                    require(password.any() { it.isLowerCase() } )
                    require(password.any() { it.isDigit() } )
                    require(password.any() { ! it.isLetterOrDigit() } )
                }

                return result.isSuccess
            }
        }
    }

}