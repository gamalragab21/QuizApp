package com.example.quizapp.activites

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.quizapp.R
import com.example.quizapp.databinding.ActivityMainBinding
import com.example.quizapp.databinding.ActivitySetupBinding
import com.example.quizapp.utils.UICommunicationHelper
import com.example.quizapp.utils.statusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , UICommunicationHelper {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBar(R.color.colorPrimary)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            hideMyProgress()
        }
        onNewIntent(intent)

    }


    open fun showMyProgress() {
        binding.layoutProgressView.isVisible = true
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    open fun hideMyProgress() {
        binding.layoutProgressView.isVisible = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun isLoading(loading: Boolean, mainActivity: Boolean) {
        if (mainActivity) {
            if (loading) {
                showMyProgress()
            } else {
                hideMyProgress()
            }
        }
    }
}