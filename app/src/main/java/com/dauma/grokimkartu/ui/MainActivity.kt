package com.dauma.grokimkartu.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dauma.grokimkartu.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBottomNavigation()
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