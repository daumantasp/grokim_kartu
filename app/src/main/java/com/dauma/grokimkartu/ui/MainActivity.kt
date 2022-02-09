package com.dauma.grokimkartu.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dauma.grokimkartu.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CustomNavigator {
    private var bottomNavigationView: BottomNavigationView? = null

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBottomNavigation()
    }

    override fun navigateToProfile() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.profileFragment)
    }

    fun changeStatusBarTheme(theme: StatusBarTheme) {
        val typedValue = TypedValue()
        val attributeId = if (theme == StatusBarTheme.LOGIN) R.attr.colorPrimaryDark else R.attr.StatusBarMainColor
        this.theme.resolveAttribute(attributeId, typedValue, true)
        val statusBarBackgroundColor = typedValue.resourceId

        window.statusBarColor = ContextCompat.getColor(this, statusBarBackgroundColor)
        // TODO: Check on API 30
        if (theme == StatusBarTheme.LOGIN) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window?.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        } else if (theme == StatusBarTheme.MAIN) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window?.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun initializeBottomNavigation() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
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
                R.id.playerDetailsFragment -> showBottomNavigation(false)
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
}

enum class StatusBarTheme {
    LOGIN,
    MAIN
}