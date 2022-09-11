package com.dauma.grokimkartu.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
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
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.general.utils.locale.LocaleUtilsImpl
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtilsImpl
import com.dauma.grokimkartu.ui.viewelements.BottomDialogViewElement
import com.dauma.grokimkartu.viewmodels.main.LanguagesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CustomNavigator, StatusBarManager, DialogsManager, BottomMenuManager {
    private var mainActivityFrameLayout: FrameLayout? = null
    private var statusBarBackgroundFrameLayout: FrameLayout? = null
    private var safeAreaConstraintLayout: ConstraintLayout? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomDialogViewElement: BottomDialogViewElement? = null
    private var currentStatusBarTheme: StatusBarTheme? = null

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityFrameLayout = findViewById<FrameLayout>(R.id.mainActivityFrameLayout)
        statusBarBackgroundFrameLayout = findViewById<FrameLayout>(R.id.statusBarBackgroundFrameLayout)
        safeAreaConstraintLayout = findViewById<ConstraintLayout>(R.id.safeAreaConstraintLayout)
        bottomDialogViewElement =
            findViewById<BottomDialogViewElement>(R.id.bottomDialogViewElement)
        initializeBottomNavigation()
        setupInsets()
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
                R.id.languagesFragment
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
            if (theme == StatusBarTheme.LOGIN) R.attr.colorPrimaryDark else R.attr.StatusBarMainColor
        this.theme.resolveAttribute(attributeId, typedValue, true)
        val statusBarBackgroundColor = typedValue.resourceId

        statusBarBackgroundFrameLayout?.setBackgroundColor(ContextCompat.getColor(this, statusBarBackgroundColor))
        // TODO: Check on API 30
        if (theme == StatusBarTheme.LOGIN) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window?.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        } else if (theme == StatusBarTheme.MAIN) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window?.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
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

    override fun refreshBottomMenuItemTitles() {
        bottomNavigationView?.menu?.let {
            it.findItem(R.id.home).title = getString(R.string.menu_home)
            it.findItem(R.id.conversations).title = getString(R.string.menu_conversations)
            it.findItem(R.id.profile).title = getString(R.string.menu_profile)
            it.findItem(R.id.settings).title = getString(R.string.menu_settings)
        }
    }
}