package com.dauma.grokimkartu.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.CodeValue
import com.dauma.grokimkartu.general.networkchangereceiver.NetworkChangeListener
import com.dauma.grokimkartu.general.networkchangereceiver.NetworkChangeReceiver
import com.dauma.grokimkartu.general.thememodemanager.ThemeManager
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.general.utils.locale.LocaleUtilsImpl
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtilsImpl
import com.dauma.grokimkartu.ui.viewelements.BottomDialogViewElement
import com.dauma.grokimkartu.viewmodels.main.LanguagesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CustomNavigator, StatusBarManager, DialogsManager,
    BottomMenuManager, NetworkChangeListener, ThemeManager {
    private var mainActivityFrameLayout: FrameLayout? = null
    private var statusBarBackgroundFrameLayout: FrameLayout? = null
    private var safeAreaConstraintLayout: ConstraintLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomDialogViewElement: BottomDialogViewElement? = null
    private var currentStatusBarTheme: StatusBarTheme? = null
    private var networkLostDialog: DialogsManager.Dialog? = null
    @Inject lateinit var networkChangeReceiver: NetworkChangeReceiver

    override val uiMode: Int
        get() = resources.configuration.uiMode

    companion object {
        val TAG = "MainActivity"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ComponentsFactory {
        fun themeModeManager() : ThemeModeManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val componentsFactory = EntryPointAccessors.fromApplication(this, ComponentsFactory::class.java)
        componentsFactory.themeModeManager().also { it.with(this) }
        setLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityFrameLayout = findViewById<FrameLayout>(R.id.main_activity_frame_layout)
        statusBarBackgroundFrameLayout = findViewById<FrameLayout>(R.id.status_bar_background_frame_layout)
        safeAreaConstraintLayout = findViewById<ConstraintLayout>(R.id.safe_area_constraint_layout)
        bottomDialogViewElement =
            findViewById<BottomDialogViewElement>(R.id.bottom_dialog_view_element)
        initializeBottomNavigation()
        setupInsets()
        addNetworkListenerAndShowDialogIfNeeded()
    }

    private fun setLocale() {
        // Utils injection won't help because at this stage it has not been created yet
        val currentLanguageCode = SharedStorageUtilsImpl(this).getEntry(LanguagesViewModel.CURRENT_LANGUAGE_KEY)
        val language = if (currentLanguageCode == "LT") Language.LT else Language.EN
        LocaleUtilsImpl().setLanguage(this, language)
    }

    private fun initializeBottomNavigation() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView!!.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registrationFragment,
                R.id.forgotPasswordFragment,
                R.id.deleteUserFragment,
                R.id.passwordChangeFragment,
                R.id.playersFragment,
                R.id.playerDetailsFragment,
                R.id.thomannFragment,
                R.id.thomannEditFragment,
                R.id.thomannDetailsFragment,
                R.id.profileEditFragment,
                R.id.notificationsFragment,
                R.id.conversationFragment,
                R.id.languagesFragment,
                R.id.conversationFragment2,
                R.id.playersFilterFragment,
                R.id.thomannsFilterFragment
                -> showBottomNavigation(false)
                else -> showBottomNavigation(true)
            }
        }
    }

    private fun showBottomNavigation(show: Boolean) {
        if (bottomNavigationView == null) {
            Log.d(TAG, "ERROR: bottomNavigationView is not initialized")
        }

        bottomNavigationView!!.visibility = if (show == true) View.VISIBLE else View.GONE
    }

    private fun setupInsets() {
        // Read more about edge to edge screen at:
        // https://developer.android.com/training/gestures/edge-to-edge
        // https://speakerdeck.com/dtrung98/windowinsets-the-way-for-android-app-to-offer-edge-to-edge-experience?slide=46
        // https://stackoverflow.com/questions/60475355/right-way-to-get-insets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        var insetTop: Int
        var insetBottom: Int
        ViewCompat.setOnApplyWindowInsetsListener(mainActivityFrameLayout!!.rootView) { v, windowInsets ->
            insetTop = windowInsets.systemWindowInsetTop
            insetBottom = windowInsets.systemWindowInsetBottom
//            stableInsetBottom do not take into account keyboard
//            insetBottom = windowInsets.stableInsetBottom

            safeAreaConstraintLayout?.setPadding(0, insetTop, 0, insetBottom)
            bottomDialogViewElement?.setPadding(0, 0, 0, insetBottom)
            val statusBarLp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, insetTop)
            statusBarBackgroundFrameLayout?.layoutParams = statusBarLp

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun addNetworkListenerAndShowDialogIfNeeded() {
        networkChangeReceiver.addListener(this)
        if (networkChangeReceiver.isNetworkAvailable() == false) {
            onNetworkLost()
        }
    }

    // MARK: CustomNavigator
    override fun navigateToProfile() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.profile)
    }

    // MARK: StatusBarManager
    override fun changeStatusBarTheme(theme: StatusBarTheme) {
        if (currentStatusBarTheme == theme) {
            return
        }

        currentStatusBarTheme = theme
        val typedValue = TypedValue()
        val attributeId =
            if (theme == StatusBarTheme.LOGIN) R.attr.colorPrimaryDark else R.attr.main_status_bar_color
        this.theme.resolveAttribute(attributeId, typedValue, true)
        val statusBarBackgroundColor = typedValue.resourceId

        statusBarBackgroundFrameLayout?.setBackgroundColor(ContextCompat.getColor(this, statusBarBackgroundColor))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // For navigation bar. Do not understand whats happening. Usually works but sometimes not on API 30
            // https://stackoverflow.com/questions/64481841/android-api-level-30-setsystembarsappearance-doesnt-overwrite-theme-data
            if (theme == StatusBarTheme.LOGIN) {
                window?.decorView?.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
            } else if (theme == StatusBarTheme.MAIN) {
                window?.decorView?.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS or APPEARANCE_LIGHT_NAVIGATION_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS or APPEARANCE_LIGHT_NAVIGATION_BARS)
            }
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            if (theme == StatusBarTheme.LOGIN) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else if (theme == StatusBarTheme.MAIN) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    // MARK: NetworkChangeListener
    override fun onNetworkAvailable() {
        if (networkLostDialog != null) {
            networkLostDialog?.dismiss()
            networkLostDialog = null
        }
    }

    override fun onNetworkLost() {
        if (networkLostDialog == null) {
            networkLostDialog = showSimpleDialog(SimpleDialogData(getString(R.string.dialog_network_lost_text), false))
        }
    }

    // MARK: DialogsManager
    override fun showBottomDialog(data: BottomDialogData) {
        bottomDialogViewElement?.let { dialog ->
            bottomNavigationView?.translationZ = -30.0f

            dialog.bindValueData(data)
            dialog.setSaveButtonEnabled(false)
            dialog.show(animated = true)
        }
    }

    override fun showBottomDatePickerDialog(data: BottomDialogDatePickerData) {
        bottomDialogViewElement?.let { dialog ->
            bottomNavigationView?.translationZ = -30.0f

            dialog.bindDatePickerData(data)
            dialog.show(animated = true)
        }
    }

    override fun showBottomCodeValueDialog(data: BottomDialogCodeValueData) {
        bottomDialogViewElement?.let { dialog ->
            bottomNavigationView?.translationZ = -30.0f

            dialog.bindCodeValueData(data)
            dialog.show(animated = true)
        }
    }

    override fun showBottomAmountDialog(data: BottomDialogAmountData) {
        bottomDialogViewElement?.let { dialog ->
            bottomNavigationView?.translationZ = -30.0f

            dialog.bindAmountData(data)
            dialog.show(animated = true)
        }
    }

    override fun setCodeValues(codeValues: List<CodeValue>) {
        bottomDialogViewElement?.let { dialog ->
            dialog.setCodeValues(codeValues)
        }
    }

    override fun hideBottomDialog() {
        bottomDialogViewElement?.let { dialog ->
            dialog.hide(animated = true) {
                changeStatusBarTheme(StatusBarTheme.MAIN)
                bottomNavigationView?.translationZ = 0.0f
            }
        }
    }

    override fun enableBottomDialogSaveButton(isEnabled: Boolean) {
        bottomDialogViewElement?.setSaveButtonEnabled(isEnabled)
    }

    override fun showBottomDialogLoading(show: Boolean) {
        bottomDialogViewElement?.showLoading(show)
    }

    override fun showSimpleDialog(data: SimpleDialogData): DialogsManager.Dialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogCancelListener = DialogInterface.OnCancelListener { dialogInterface ->
            data.onCancelClicked()
            dialogInterface.dismiss()
        }
        val dialog = alertDialogBuilder.setMessage(data.text)
            .setCancelable(data.cancelable)
            .setOnCancelListener(dialogCancelListener)
            .show()

        return object : DialogsManager.Dialog {
            override fun dismiss() {
                dialog.dismiss()
            }
        }
    }
    override fun showYesNoDialog(data: YesNoDialogData) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogClickListener = DialogInterface.OnClickListener { dialogInterface, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> data.onPositiveButtonClick()
                DialogInterface.BUTTON_NEGATIVE -> data.onNegativeButtonClick()
            }
            dialogInterface.dismiss()
        }
        val dialogCancelListener = DialogInterface.OnCancelListener { dialogInterface ->
            data.onCancelClicked()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setMessage(data.text)
            .setCancelable(data.cancelable)
            .setPositiveButton(data.positiveText, dialogClickListener)
            .setNegativeButton(data.negativeText, dialogClickListener)
            .setOnCancelListener(dialogCancelListener)
            .show()
    }

    override fun refreshBottomMenuItemTitles() {
        bottomNavigationView?.menu?.let {
            it.findItem(R.id.home).title = getString(R.string.menu_home)
            it.findItem(R.id.conversations).title = getString(R.string.menu_conversations)
            it.findItem(R.id.profile).title = getString(R.string.menu_profile)
            it.findItem(R.id.settings).title = getString(R.string.menu_settings)
        }
    }
}