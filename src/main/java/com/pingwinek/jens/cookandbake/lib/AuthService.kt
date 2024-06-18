package com.pingwinek.jens.cookandbake.lib

interface AuthService {

    enum class AuthActionResult {
        DELETE_SUCCEEDED,
        EXC_DATAPOLICY_NOT_ACCEPTED,
        EXC_DELETE_FAILED_RECENT_LOGIN_REQUIRED,
        EXC_DELETE_FAILED_WITHOUT_REASON,
        EXC_EMAIL_EMPTY_OR_MALFORMATTED,
        EXC_NO_SIGNEDIN_USER,
        EXC_PASSWORD_EMPTY,
        EXC_PASSWORD_POLICY_CHECK_NOT_PASSED,
        EXC_REGISTRATION_FAILED_WITHOUT_REASON,
        EXC_RESET_OR_VERIFY_CODE_INVALID,
        EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON,
        EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON,
        EXC_SIGNIN_FAILED_WITHOUT_REASON,
        EXC_SIGNOUT_FAILED_WITHOUT_REASON,
        EXC_USER_ALREADY_EXISTS,
        EXC_VERIFICATION_FAILED_WITHOUT_REASON,
        EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON,
        NOTHING,
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

    interface AuthenticationListener {
        fun onLogin()
        fun onLogout()
    }

    class PasswordPolicy {

        companion object {

            private val minLength = 8

            fun getPasswordPolicy(policyString: String): String {
                return policyString.format(minLength, 1, 1, 1, 1)
            }

            fun matches(password: String): Boolean {
                val result = runCatching {
                    require(password.length >= minLength)
                    require(password.none { it.isWhitespace() } )
                    require(password.any { it.isUpperCase() } )
                    require(password.any { it.isLowerCase() } )
                    require(password.any { it.isDigit() } )
                    require(password.any { ! it.isLetterOrDigit() } )
                }

                return result.isSuccess
            }
        }
    }

}