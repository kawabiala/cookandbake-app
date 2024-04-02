package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.LabelledCheckBox
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.SpacerMedium
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.SpacerSmall
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel
import java.util.LinkedList

class SignInActivity : BaseActivity() {

    private enum class ButtonRightAction {
        NOTHING,
        REGISTER,
        RESETPASSWORD,
        SENDVERIFICATION,
        SIGNIN,
    }

    /**
     *
     */
    private data class ViewSettings(
        val caption: String? = null,
        val showTabRow: Boolean = true,
        val selectLeftTab: Boolean = true,
//        val buttonLeftCaption: String = "",
        val buttonRightCaption: String = "",
//        val buttonLeftAction: () -> Unit = {},
        val buttonRightAction: ButtonRightAction = ButtonRightAction.NOTHING,
        val editEmail: Boolean = true,
        val showPassword: Boolean = true,
        val showCrashlytics: Boolean = true,
        val showAccountSettings: Boolean = false,
        val showReset: Boolean = false,
        val showPrivacy: Boolean = true,
        val showRightButton: Boolean = true,
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

        // Define view settings

        registerView = ViewSettings(
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.register),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.REGISTER
        )

        signInView = ViewSettings(
            selectLeftTab = false,
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.login),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SIGNIN,
            showCrashlytics = false,
            showReset = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        resetPasswordView = ViewSettings(
            caption = getString(R.string.setPassword),
            showTabRow = false,
//            buttonLeftCaption = getString(R.string.close),
            buttonRightCaption = getString(R.string.setPassword),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.RESETPASSWORD,
            editEmail = false,
            showReset = true,
            showCrashlytics = false,
            showPrivacy = false
        )

        unverifiedView = ViewSettings(
            caption = getString(R.string.registrationIncomplete),
            showTabRow = false,
//            buttonLeftCaption =  getString(R.string.close),
            buttonRightCaption = getString(R.string.sendVerificationEmail),
//            buttonLeftAction = closeAction,
            buttonRightAction = ButtonRightAction.SENDVERIFICATION,
            editEmail = false,
            showPassword = false,
            showCrashlytics = false,
            showAccountSettings = true,
            showPrivacy = false,
            showLogout = true,
            showDelete = true
        )

        verifiedView = ViewSettings(
            caption = getString(R.string.profile),
            showTabRow = false,
            editEmail = false,
            showPassword = false,
            showAccountSettings = true,
            showCrashlytics = false,
            showReset = true,
            showPrivacy = false,
            showRightButton = false,
            showLogout = true,
            showDelete = true
        )

        // Observers

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        userInfoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(UserInfoViewModel::class.java)

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

        configureTopBar(title = getString(R.string.profile))

        val optionItems = mutableListOf(
            PingwinekCooksComposables.OptionItem(
                getString(R.string.dataprotection),
                Icons.Filled.Lock
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.dataprotection))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)
                        )
                )
            },
            PingwinekCooksComposables.OptionItem(
                getString(R.string.impressum),
                Icons.Filled.Info
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.impressum))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)
                        )
                )
            }
        )

        configureDropDown(*optionItems.toTypedArray())

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
    override fun ScaffoldContent(paddingValues: PaddingValues) {

        val authStatus = authenticationViewModel.authStatus.observeAsState()
        val linkMode = authenticationViewModel.linkMode.observeAsState()
        val authResult = authenticationViewModel.authActionResult.observeAsState()
        val email = authenticationViewModel.email.observeAsState()
        val newEmail = authenticationViewModel.newEmail.observeAsState()
        val password = authenticationViewModel.password.observeAsState()
        val isPrivacyApproved = authenticationViewModel.isPrivacyApproved.observeAsState()

        val userInfoData = userInfoViewModel.userInfoData.observeAsState()

        var asRegistration: Boolean by remember { mutableStateOf(authResult.value == AuthService.AuthActionResult.DELETE_SUCCEEDED) }

        val viewSettings = determineViewSetting(authStatus, linkMode, asRegistration)

        val toggleRegistrationView : () -> Unit = { asRegistration = !asRegistration }

        val onResetPasswordClicked : () -> Unit = {
            sendResetEmailAction(if (viewSettings.editEmail) newEmail.value ?: "" else email.value ?: "")
        }

        val onCrashlyticsChange : (checked : Boolean) -> Unit = { checked -> userInfoViewModel.saveUserInfo(checked) }

        val onButtonRightChange : () -> Unit = {
            when (viewSettings.buttonRightAction) {
                ButtonRightAction.SIGNIN -> { signInAction() }
                ButtonRightAction.REGISTER -> { registerAction() }
                ButtonRightAction.RESETPASSWORD -> { resetPasswordAction() }
                ButtonRightAction.SENDVERIFICATION -> { sendEmailVerificationAction() }
                ButtonRightAction.NOTHING -> {}
            }
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            SpacerMedium()

            Row {
                if (!viewSettings.caption.isNullOrEmpty()) {
                    ProfileHeader(
                        text = viewSettings.caption
                    )
                }

                if (viewSettings.showTabRow)  {
                    SignInTabRow(viewSettings.selectLeftTab, toggleRegistrationView)
                }
            }

            SpacerMedium()

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
                PingwinekCooksComposables.PasswordField(
                    password = password.value ?: "",
                    label = getString(R.string.password),
                    onValueChange = { authenticationViewModel.onPasswordChange(it) },
                )
            }

            if (viewSettings.showPrivacy) {
                SpacerMedium()
                LabelledCheckBox(
                    label = getString(R.string.declareAcceptanceOfDataprotection),
                    checked = isPrivacyApproved.value ?: false,
                    onCheckedChange = { authenticationViewModel.onIsPrivacyApprovedChange(it) }
                )
            }

            if (viewSettings.showCrashlytics) {
                SpacerSmall()
                LabelledCheckBox(
                    label = getString(R.string.acceptCrashlytics),
                    checked = userInfoData.value?.crashlyticsEnabled ?: false,
                    onCheckedChange = onCrashlyticsChange
                )

            }

            SpacerMedium()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val buttonLeftColors = if (viewSettings.showRightButton) {
                    ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }

                Button(
                    colors = buttonLeftColors,
                    onClick = closeAction
                ) {
                    Text(getString(R.string.close))
                }

                if (viewSettings.showRightButton) {
                    Button(
                        onClick = onButtonRightChange
                    ) {
                        Text(viewSettings.buttonRightCaption)
                    }
                }
            }

            if (viewSettings.showAccountSettings) {
                SpacerMedium()
                AccountSettingsBox(
                    crashlyticsEnabled = userInfoData.value?.crashlyticsEnabled ?: false,
                    onCrashlyticsChange = onCrashlyticsChange,
                    onResetPasswordClicked = onResetPasswordClicked
                )
            }
        }
    }

    @Composable
    fun ProfileHeader(
        text: String
    ) {
        Row() {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
                )
        }
    }

    @Composable
    fun SignInTabRow(
        highlightLeft: Boolean,
        toggleItem: () -> Unit
    ) {
        PingwinekCooksComposables.PingwinekCooksTabRow(
            selectedItem = if (highlightLeft) { 0 } else { 1 },
            menuItems = LinkedList<PingwinekCooksComposables.OptionItem>().apply {
                add(
                    PingwinekCooksComposables.OptionItem(
                        getString(R.string.register), Icons.Filled.Person, toggleItem
                    ))
                add(
                    PingwinekCooksComposables.OptionItem(
                        getString(R.string.login), Icons.Filled.Person, toggleItem
                    ))
            }
        )
    }

    @Composable
    fun AccountSettingsBox(
        crashlyticsEnabled: Boolean,
        onCrashlyticsChange: (Boolean) -> Unit,
        onResetPasswordClicked: () -> Unit
    ) {
        PingwinekCooksComposables.Expandable(
            headerText = "Account Settings",
            headerTextStyle = MaterialTheme.typography.headlineMedium,
            headerTextColor = MaterialTheme.colorScheme.onSurface,
            contentTextStyle = MaterialTheme.typography.bodyMedium,
            boxColor = MaterialTheme.colorScheme.surfaceContainerLow,
            padding = Dp(20F)
        ) { contentTextStyle ->
            PingwinekCooksComposables.LabelledSwitch(
                label = getString(R.string.acceptCrashlytics),
                labelTextStyle = contentTextStyle,
                checked = crashlyticsEnabled,
                onCheckedChange = onCrashlyticsChange
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            Row(
                modifier = Modifier.clickable { onResetPasswordClicked() }
            ) {
                Text(
                    text = getString(R.string.lostPassword),
                    style = contentTextStyle,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )


            Row(
                Modifier.clickable(
                    onClick = signOutAction
                )
            ) {
                Text(
                    text = getString(R.string.logout),
                    style = contentTextStyle,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            Row(
                Modifier.clickable(
                    onClick = deleteAction
                )
            ) {
                Text(
                    text = getString(R.string.delete),
                    style = contentTextStyle,
                )
            }
        }
    }

    private fun determineViewSetting(
        authStatus: State<AuthService.AuthStatus?>,
        linkMode: State<AuthenticationViewModel.EmailLinkMode?>,
        asRegistration: Boolean
        ) : ViewSettings {
        return when (authStatus.value) {
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