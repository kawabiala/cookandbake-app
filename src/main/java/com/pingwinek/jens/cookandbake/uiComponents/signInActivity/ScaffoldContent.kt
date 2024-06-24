package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    authenticationViewModel: AuthenticationViewModel,
    userInfoViewModel: UserInfoViewModel,
    onClose: () -> Unit
) {
    val authStatus by authenticationViewModel.authStatus.observeAsState()
    val linkMode by authenticationViewModel.linkMode.observeAsState()
    val authResult by authenticationViewModel.authActionResult.observeAsState()
    val email by authenticationViewModel.email.observeAsState()

    val userInfoData by userInfoViewModel.userInfoData.observeAsState()

    var asRegistration: Boolean by remember { mutableStateOf(authResult == AuthService.AuthActionResult.DELETE_SUCCEEDED) }
    var showResetRequest by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val view by remember(authStatus, linkMode, asRegistration, showResetRequest) {
        derivedStateOf {
            determineViewSetting(authStatus, linkMode, asRegistration, showResetRequest)
        }
    }

    val authActionResult by remember(authResult) {
        mutableStateOf(authResult)
    }

    val toggleRegistrationView : () -> Unit = { asRegistration = !asRegistration }

    val onDelete: () -> Unit = {
        showDeleteDialog = true
    }

    val onLogin: (email: String, password: String) -> Unit = { loginEmail, loginPw ->
        authenticationViewModel.signIn(loginEmail, loginPw)
    }

    val onLogout: () -> Unit = {
        authenticationViewModel.signOut()
    }

    val onRegister: (email: String, password: String, dataPolicyChecked: Boolean, crashlyticsEnabled: Boolean) -> Unit = {
        registerEmail, registerPw, registerDataPolicyChecked, registerCrashlyticsEnabled ->
        authenticationViewModel.register(registerEmail, registerPw, registerDataPolicyChecked)
        userInfoViewModel.saveUserInfo(registerCrashlyticsEnabled)
    }

    val onResetPassword : (password: String) -> Unit = { resetPw ->
        authenticationViewModel.resetPassword(resetPw)
    }

    val onResetPasswordRequestForEmail: (email: String) -> Unit = { resetEmail ->
        authenticationViewModel.sendPasswordResetEmail(resetEmail)
    }

    val onShowResetPasswordView: (Boolean) -> Unit = { show ->
        showResetRequest = show
    }

    val onVerify: () -> Unit = {
        authenticationViewModel.sendVerificationEmail()
    }

    val onCrashlyticsChange : (checked : Boolean) -> Unit = { checked -> userInfoViewModel.saveUserInfo(checked) }

    val onDialogDismissed : () -> Unit = {
        authenticationViewModel.invalidateResult()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(scrollState)
    ) {

        SpacerMedium()

        if (authActionResult != null) {
            val message = getMessage(authActionResult)

            message?.let { msg ->
                BasicAlertDialog(
                    onDismissRequest = onDialogDismissed,
                    properties = DialogProperties(dismissOnBackPress = true),
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(all = MaterialTheme.spacing.standardPadding)
                ) {
                    Text(msg)
                }
            }
        }

        if (showDeleteDialog) {
            DeleteDialog(
                message = stringResource(R.string.confirmUnsubscribe),
                onClose = { showDeleteDialog = false },
                onDelete = { authenticationViewModel.deleteAccount() }
            )
        }

        when (view) {
            View.LOGIN -> {
                LoginView(
                    email = email ?: "",
                    onResetPassword = { onShowResetPasswordView(true) },
                    onLogin = onLogin,
                    onClose = onClose
                )
            }

            View.REGISTER -> {
                RegisterView(
                    onRegister = onRegister,
                    onClose = onClose
                )
            }

            View.RESET -> {
                ResetPasswordView(
                    email = email ?: "",
                    onReset = onResetPassword,
                    onClose = onClose
                )
            }

            View.RESET_REQUEST -> {
                ResetPasswordRequestView(
                    email = email ?: "",
                    onResetRequest = onResetPasswordRequestForEmail,
                    onClose = onClose
                )
            }

            View.UNVERIFIED -> {
                UnverifiedView(
                    email = email ?: "",
                    crashlyticsEnabled = userInfoData?.crashlyticsEnabled ?: false,
                    onCrashlyticsChange = onCrashlyticsChange,
                    onVerify = onVerify,
                    onResetPasswordRequest = { onShowResetPasswordView(true) },
                    onLogout = onLogout,
                    onDelete = onDelete,
                    onClose = onClose
                )
            }

            View.VERIFIED -> {
                VerifiedView(
                    email = email ?: "",
                    crashlyticsEnabled = userInfoData?.crashlyticsEnabled ?: false,
                    onCrashlyticsChange = onCrashlyticsChange,
                    onResetPasswordRequest = { onShowResetPasswordView(true) },
                    onLogout = onLogout,
                    onDelete = onDelete) {

                }
            }
        }
    }
}

private fun determineViewSetting(
    authStatus: AuthService.AuthStatus?,
    linkMode: AuthenticationViewModel.EmailLinkMode?,
    asRegistration: Boolean,
    showResetPasswordRequest: Boolean
) : View {
    return when (authStatus) {
        AuthService.AuthStatus.VERIFIED -> {
            if (linkMode == AuthenticationViewModel.EmailLinkMode.RESET) {
                View.RESET
            } else if (showResetPasswordRequest) {
                View.RESET_REQUEST
            } else {
                View.VERIFIED
                //signInView
                //registerView
            }
        }

        AuthService.AuthStatus.SIGNED_IN -> {
            if (linkMode == AuthenticationViewModel.EmailLinkMode.RESET) {
                View.RESET
            } else if (linkMode == AuthenticationViewModel.EmailLinkMode.VERIFIED) {
                View.VERIFIED
            } else if (showResetPasswordRequest) {
                View.RESET_REQUEST
            } else {
                View.UNVERIFIED
            }
        }

        AuthService.AuthStatus.SIGNED_OUT -> {
            if (linkMode == AuthenticationViewModel.EmailLinkMode.RESET) {
                View.RESET
            } else if (asRegistration) {
                View.REGISTER
            } else if (showResetPasswordRequest) {
                View.RESET_REQUEST
            } else {
                View.LOGIN
            }
        }

        else -> {
            View.LOGIN
        }
    }
}

@Composable
private fun getMessage(authResult: AuthService.AuthActionResult?) : String? {
    return when (authResult) {
        AuthService.AuthActionResult.DELETE_SUCCEEDED -> stringResource(R.string.accountDeleted)
        AuthService.AuthActionResult.REGISTRATION_SUCCEEDED -> stringResource(R.string.accountCreated)
        AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> stringResource(R.string.PasswordChanged)
        AuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED -> stringResource(R.string.lostPasswordSentLong)
        AuthService.AuthActionResult.SIGNIN_SUCCEEDED -> stringResource(R.string.loggedIn)
//            AuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> , stringResource(R.string.loggedOut)
        AuthService.AuthActionResult.VERIFICATION_SUCCEEDED -> stringResource(R.string.confirmationSucceeded)
        AuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> stringResource(R.string.confirmationSent)
        AuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED -> stringResource(R.string.dataProtectionNotChecked)
        AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON -> stringResource(R.string.deleteFailed)
        AuthService.AuthActionResult.EXC_DELETE_FAILED_RECENT_LOGIN_REQUIRED -> stringResource(R.string.login_again)
        AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> stringResource(R.string.emailMalformatted)
        AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> stringResource(R.string.noSignedInUser)
        AuthService.AuthActionResult.EXC_PASSWORD_EMPTY -> stringResource(
            R.string.passwordMalformatted,
            AuthService.PasswordPolicy.getPasswordPolicy(stringResource(R.string.passwordPolicy))
        )
        AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> stringResource(
            R.string.passwordMalformatted,
            AuthService.PasswordPolicy.getPasswordPolicy(stringResource(R.string.passwordPolicy))
        )
        AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON -> stringResource(R.string.registrationFailed)
        AuthService.AuthActionResult.EXC_RESET_OR_VERIFY_CODE_INVALID -> stringResource(R.string.resetOrVerifyCodeMalformed)
        AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON -> stringResource(R.string.resetFailed)
        AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON -> stringResource(R.string.sendResetFailed)
        AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON -> stringResource(R.string.loginFailed)
        AuthService.AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON -> stringResource(R.string.logoutFailed)
        AuthService.AuthActionResult.EXC_USER_ALREADY_EXISTS -> stringResource(R.string.userAlreadyExists)
        AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON -> stringResource(R.string.verificationFailed)
        AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON -> stringResource(R.string.sendVerificationFailed)
        else -> null
    }
}
