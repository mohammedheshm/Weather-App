package com.example.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var fragmentContainerView: FragmentContainerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentContainerView = findViewById(R.id.nav_host_fragment)

        initUi()
    }

    private fun initUi() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        val navOptions = NavOptions.Builder()
            .setRestoreState(true)
            .setPopUpTo(R.id.home, false, true)
            .build()

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.home_graph, null, navOptions)
                R.id.alert -> navController.navigate(R.id.alert_graph, null, navOptions)
                R.id.favorite -> navController.navigate(R.id.favorite_graph, null, navOptions)
                R.id.settings -> navController.navigate(
                    R.id.preferences_graph,
                    null,
                    navOptions
                )
            }
            true
        }

    }
}