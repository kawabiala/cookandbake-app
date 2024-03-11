package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel
import java.util.LinkedList

class SignInActivity : BaseActivity() {

    private enum class ButtonRightAction {
        CLOSE,
        REGISTER,
        RESETPASSWORD,
        SENDVERIFICATION,
        SIGNIN,
    }

    /**
     *
     */
    private data class ViewSettings(
        val headerLeftCaption: String = "",
        val headerRightCaption: String = "",
        val buttonLeftCaption: String = "",
        val buttonRightCaption: String = "",
        val buttonLeftAction: () -> Unit = {},
        val buttonRightAction: ButtonRightAction = ButtonRightAction.CLOSE,
        val highlightLeftHeader: Boolean = true,
        val showOnlyLeftHeader: Boolean = false,
        val editEmail: Boolean = true,
        val showPassword: Boolean = true,
        val showCrashlytics: Boolean = true,
        val showReset: Boolean = false,
        val showPrivacy: Boolean = true,
        val showLeftButton: Boolean = true,
        val showLogout: Boolean = false,
        val showDelete: Boolean = false
    )

    private lateinit var registerView: ViewSettings
    private lateinit var signInView: ViewSettings
    private lateinit var resetPasswordView: ViewSettings
    private lateinit var unverifiedView: ViewSettings
    private lateinit var verifiedView: ViewSettings

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel

    private var asRegistrationView: Boolean = true

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifecycle Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        userInfoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(UserInfoViewModel::class.java)

        // Define view settings

        registerView = ViewSettings(
            headerLeftCaption = getString(R.string.register),
            headerRightCaption = getString(R.string.login),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.register),
            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.REGISTER
        )

        signInView = ViewSettings(
            headerLeftCaption = getString(R.string.register),
            headerRightCaption = getString(R.string.login),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.login),
            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SIGNIN,
            highlightLeftHeader = false,
            showReset = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        resetPasswordView = ViewSettings(
            headerLeftCaption = getString(R.string.setPassword),
            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.setPassword),
            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.RESETPASSWORD,
            editEmail = false,
            showOnlyLeftHeader = true,
            showReset = true,
            showCrashlytics = false,
            showPrivacy = false
        )

        unverifiedView = ViewSettings(
            headerLeftCaption = getString(R.string.registrationIncomplete),
            buttonLeftCaption =  getString(R.string.close),
            buttonRightCaption = getString(R.string.sendVerificationEmail),
            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SENDVERIFICATION,
            showOnlyLeftHeader = true,
            editEmail = false,
            showPassword = false,
            showCrashlytics = false,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        verifiedView = ViewSettings(
            headerLeftCaption = getString(R.string.profile),
            buttonRightCaption = getString(R.string.close),
            buttonRightAction = ButtonRightAction.CLOSE,
            showOnlyLeftHeader = true,
            editEmail = false,
            showPassword = false,
            showReset = true,
            showPrivacy = false,
            showLeftButton = false,
            showLogout = true,
            showDelete = true
        )

        // Observers

        authenticationViewModel.authActionResult.observe(this) {
            when (authenticationViewModel.authActionResult.value) {
                AuthService.AuthActionResult.DELETE_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.accountDeleted)) {
                        asRegistrationView = true
                        //resetView()
                    }
                }
                AuthService.AuthActionResult.REGISTRATION_SUCCEEDED -> {
                    toast(getString(R.string.accountCreated))
                }
                AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.PasswordChanged)) {
                        asRegistrationView = false
                        //applyViewSettings(signInView) // resetView won't work in this case
                    }
                }
                AuthService.AuthActionResult.RESET_PASSWORD_SEND_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.lostPasswordSentLong))
                }
                AuthService.AuthActionResult.SIGNIN_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.loggedIn),
                        closeAction)
                }
                AuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.loggedOut)) {
                        asRegistrationView = false
                        //resetView()
                    }
                }
                AuthService.AuthActionResult.VERIFICATION_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.confirmationSucceeded)) {
                        closeAction
                    }
                }
                AuthService.AuthActionResult.VERIFICATION_SEND_SUCCEEDED -> {
                    authMessage(
                        getString(R.string.confirmationSent),
                        closeAction)
                }
                AuthService.AuthActionResult.EXC_DATAPOLICY_NOT_ACCEPTED -> {
                    errorMessage(
                        getString(R.string.dataProtectionNotChecked)
                    )
                }
                AuthService.AuthActionResult.EXC_DELETE_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.deleteFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_EMAIL_EMPTY_OR_MALFORMATTED -> {
                    errorMessage(
                        getString(R.string.emailMalformatted)
                    )
                }
                AuthService.AuthActionResult.EXC_NO_SIGNEDIN_USER -> {
                    errorMessage(
                        getString(R.string.noSignedInUser)
                    )
                }
                AuthService.AuthActionResult.EXC_PASSWORD_EMPTY -> {
                    errorMessage(
                        getString(
                            R.string.passwordMalformatted,
                            AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                        )
                    )
                }
                AuthService.AuthActionResult.EXC_PASSWORD_POLICY_CHECK_NOT_PASSED -> {
                    errorMessage(
                        getString(
                            R.string.passwordMalformatted,
                            AuthService.PasswordPolicy.getPasswordPolicy(getString(R.string.passwordPolicy))
                        )
                    )
                }
                AuthService.AuthActionResult.EXC_REGISTRATION_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.registrationFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.resetFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_RESET_PASSWORD_SEND_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.sendResetFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_SIGNIN_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.loginFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_SIGNOUT_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.logoutFailed)
                    )
                }
                AuthService.AuthActionResult.EXC_USER_ALREADY_EXISTS -> TODO()
                AuthService.AuthActionResult.EXC_VERIFICATION_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.verificationFailed)) {
                        //applyViewSettings(unverifiedView)
                    }
                }
                AuthService.AuthActionResult.EXC_VERIFICATION_SEND_FAILED_WITHOUT_REASON -> {
                    errorMessage(
                        getString(R.string.sendVerificationFailed)
                    )
                }
                null -> TODO()
            }
        }
        /*

                authenticationViewModel.linkMode.observe(this) {
                    when (it) {
                        AuthenticationViewModel.EmailLinkMode.RESET -> applyViewSettings(resetPasswordView)
                        AuthenticationViewModel.EmailLinkMode.VERIFIED -> applyViewSettings(verifiedView)
                        else -> {}
                    }
                }
                authenticationViewModel.email.observe(this) {
                    with(emailEditText.text) {
                        clear()
                        append(authenticationViewModel.email.value)
                    }
                }
                authenticationViewModel.authStatus.observe(this) { authStatus ->
                    when (authStatus) {
                        AuthService.AuthStatus.SIGNED_OUT -> {
                            if (currentView != resetPasswordView) resetView()
                        }
                        AuthService.AuthStatus.SIGNED_IN -> {
                            if (currentView != resetPasswordView) applyViewSettings(unverifiedView)
                        }
                        AuthService.AuthStatus.VERIFIED -> {
                            if (currentView != resetPasswordView) applyViewSettings(verifiedView)
                        }
                        else -> {}
                    }
                }
                userInfoViewModel.userInfoData.observe(this) { userInfo ->
                    acceptCrashlyticsView.isChecked = userInfo.crashlyticsEnabled
                }

         */
        configureTopBar(title = getString(R.string.profile))
        configureNavigationBar(
            selectedItem = Navigation.LOGIN,
            onRecipeClickAction = closeAction
            )
    }

    override fun onResume() {
        super.onResume()

        authenticationViewModel.checkAuthStatus()
        authenticationViewModel.retrieveEmail()
        authenticationViewModel.checkActionCodeForIntent(intent)

        userInfoViewModel.loadData()

    }

    /**
     * Called before onResume:
     * if there is a new intent, update the intent for this activity - otherwise the original
     * intent would be used; compare documentation in [android.app.Activity]
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            this.intent = it
        }
    }

    @Preview
    @Composable
    fun Preview() {
        super.BasePreview()
    }

    @Composable
    override fun ScaffoldContent() {
        Screen { viewSettings, toggleRegistrationView ->
            val email = authenticationViewModel.email.observeAsState()
            val newEmail = authenticationViewModel.newEmail.observeAsState()
            val password = authenticationViewModel.password.observeAsState()
            val isPrivacyApproved = authenticationViewModel.isPrivacyApproved.observeAsState()

            val onResetPasswordClicked : () -> Unit = {
                sendResetEmailAction(if (viewSettings.editEmail) newEmail.value ?: "" else email.value ?: "")
            }

            val userInfoData = userInfoViewModel.userInfoData.observeAsState()
            val onCrashlyticsChange : (checked : Boolean) -> Unit = { checked -> userInfoViewModel.saveUserInfo(checked) }

            val onButtonRightChange : () -> Unit = {
                when (viewSettings.buttonRightAction) {
                    ButtonRightAction.SIGNIN -> { signInAction() }
                    ButtonRightAction.REGISTER -> { registerAction() }
                    ButtonRightAction.RESETPASSWORD -> { resetPasswordAction() }
                    ButtonRightAction.SENDVERIFICATION -> { sendEmailVerificationAction() }
                    ButtonRightAction.CLOSE -> { closeAction() }
                    else -> {}
                }
            }

            Row(
                modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
            ) {
                if (viewSettings.showOnlyLeftHeader) {
                    ProfileHeader(
                        text = viewSettings.headerLeftCaption
                    )
                } else {
                    SignInTabRow(viewSettings.headerLeftCaption, viewSettings.headerRightCaption, viewSettings.highlightLeftHeader, toggleRegistrationView)
                }
            }

            PingwinekCooksComposables.EditableText(
                text = if (viewSettings.editEmail) {
                    newEmail.value ?: ""
                } else {
                    getString(R.string.logged_in_as, email.value ?: "")
                },
                label = getString(R.string.email),
                editable = viewSettings.editEmail,
                onValueChange = { authenticationViewModel.onNewEmailChange(it) }
            )

            if (viewSettings.showPassword) {
                PingwinekCooksComposables.EditableText(
                    text = password.value ?: "",
                    label = getString(R.string.password),
                    supportingText = if (viewSettings.showReset) getString(R.string.lostPassword) else null,
                    editable = true,
                    onValueChange = { authenticationViewModel.onPasswordChange(it) },
                    onSupportingTextClicked = onResetPasswordClicked
                )
            } else if (viewSettings.showReset) {
                Row(
                    modifier = Modifier.clickable { onResetPasswordClicked() }
                ) {
                    Text(text = getString(R.string.lostPassword))
                }
            }

            if (viewSettings.showPrivacy) {
                PingwinekCooksComposables.LabelledCheckBox(
                    label = getString(R.string.declareAcceptanceOfDataprotection),
                    checked = isPrivacyApproved.value ?: false,
                    onCheckedChange = { authenticationViewModel.onIsPrivacyApprovedChange(it) }
                )
            }

            if (viewSettings.showCrashlytics) {
                PingwinekCooksComposables.LabelledCheckBox(
                    label = getString(R.string.acceptCrashlytics),
                    checked = userInfoData.value?.crashlyticsEnabled ?: false,
                    onCheckedChange = onCrashlyticsChange
                    )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (viewSettings.showLeftButton) {
                    Button(
                        onClick = viewSettings.buttonLeftAction
                    ) {
                        Text(viewSettings.buttonLeftCaption)
                    }
                }
                Button(
                    onClick = onButtonRightChange
                ) {
                    Text(viewSettings.buttonRightCaption)
                }
            }

            if (viewSettings.showLogout) {
                Row(
                    Modifier.clickable(
                        onClick = signOutAction
                    )
                ) {
                    Text(getString(R.string.logout))
                }
            }

            if (viewSettings.showDelete) {
                Row(
                    Modifier.clickable(
                        onClick = deleteAction
                    )
                ) {
                    Text(getString(R.string.delete))
                }
            }
        }
    }

    @Composable
    private fun Screen(
        content: @Composable (viewSettings: ViewSettings, toggleRegistration: () -> Unit) -> Unit
    ) {
        val authStatus = authenticationViewModel.authStatus.observeAsState()
        val linkMode = authenticationViewModel.linkMode.observeAsState()
        val authResult = authenticationViewModel.authActionResult.observeAsState()

        val asRegState : State<Boolean?> = remember { derivedStateOf {
            when (authResult.value) {
                AuthService.AuthActionResult.DELETE_SUCCEEDED -> true
                AuthService.AuthActionResult.RESET_PASSWORD_SUCCEEDED -> false
                AuthService.AuthActionResult.SIGNOUT_SUCCEEDED -> false
                else -> null
            }
        }}

        var asRegistration: Boolean by remember { mutableStateOf(true) }
        val toggleRegistrationView : () -> Unit = { asRegistration = !asRegistration }

        val viewSettings = when (authStatus.value) {
            AuthService.AuthStatus.VERIFIED -> {
                if (linkMode.value == AuthenticationViewModel.EmailLinkMode.RESET) {
                    resetPasswordView
                } else {
                    verifiedView
                    //signInView
                    //registerView
                }
            }

            AuthService.AuthStatus.SIGNED_IN -> {
                when (linkMode.value) {
                    AuthenticationViewModel.EmailLinkMode.RESET -> {
                        resetPasswordView
                    }

                    AuthenticationViewModel.EmailLinkMode.VERIFIED -> {
                        verifiedView
                    }

                    else -> {
                        unverifiedView
                    }
                }
            }

            AuthService.AuthStatus.SIGNED_OUT -> {
                if (asRegistration) {
                    registerView
                } else {
                    signInView
                }
            }

            else -> {
                ViewSettings()
            }
        }

        Log.i(this::class.java.name, "viewSettings: $viewSettings")

        content(viewSettings, toggleRegistrationView)
    }


    @Composable
    fun ProfileHeader(
        text: String
    ) {
        Row() {
            Text(
                text = text,
                fontSize = TextUnit(5F, TextUnitType.Em),
                fontWeight = FontWeight.Bold
                )
        }
    }

    @Composable
    fun SignInTabRow(
        leftCaption: String,
        rightCaption: String,
        highlightLeft: Boolean,
        toggleItem: () -> Unit
    ) {
        PingwinekCooksComposables.PingwinekCooksTabRow(
            selectedItem = if (highlightLeft) { 0 } else { 1 },
            menuItems = LinkedList<PingwinekCooksComposables.OptionItem>().apply {
                add(
                    PingwinekCooksComposables.OptionItem(
                        leftCaption, Icons.Filled.Person
                    ) {
                        if (!highlightLeft) toggleItem()
                    })
                add(
                    PingwinekCooksComposables.OptionItem(
                        rightCaption, Icons.Filled.Person
                    ) {
                        if (highlightLeft) toggleItem()
                    })
            }
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Auth-Actions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private val closeAction: () -> Unit = {
        startActivity(Intent(this, RecipeListingActivity::class.java))
        finish()
    }

    private val crashlyticsAction: () -> Unit = {
//        userInfoViewModel.saveUserInfo(acceptCrashlyticsView.isChecked)
    }

    private val deleteAction: () -> Unit = {
        authenticationViewModel.deleteAccount()
    }

    private val registerAction: () -> Unit = {
        authenticationViewModel.register()
    }

    private val resetPasswordAction: () -> Unit = {
        authenticationViewModel.resetPassword()
    }

    private val sendEmailVerificationAction: () -> Unit = {
        authenticationViewModel.sendVerificationEmail()
    }

    private val sendResetEmailAction: (email: String) -> Unit = { email ->
        authenticationViewModel.sendPasswordResetEmail(email)
//        val email = emailEditText.text.toString()
//        authenticationViewModel.sendPasswordResetEmail(email)
    }

    private val signInAction: () -> Unit = {
        authenticationViewModel.signIn()
    }

    private val signOutAction: () -> Unit = {
        authenticationViewModel.signOut()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Other functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun alert(title: String?, message: String, action: (() -> Unit)?) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            title?.let { setTitle(it) }
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                action?.let { it() }
            }.create().show()
        }
    }

    private fun authMessage(message: String) {
        authMessage(message) {}
    }

    private fun authMessage(message: String, action: () -> Unit) {
        alert(getString(R.string.authenticationMessage), message, action)
    }

    private fun errorMessage(message: String) {
        errorMessage(message) {}
    }

    private fun errorMessage(message: String, action: () -> Unit) {
        alert(getString(R.string.errorMessage), message, action)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}