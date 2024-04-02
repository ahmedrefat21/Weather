package com.example.weatherforecast


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.alerts.view.AlertsFragment
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.favourite.view.FavouriteFragment
import com.example.weatherforecast.home.view.HomeFragment

import com.example.weatherforecast.settings.view.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNav : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottom_navigation) as BottomNavigationView
        loadFragment(HomeFragment())

        bottomNav.selectedItemId = R.id.home
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.favourite -> {
                    loadFragment(FavouriteFragment())
                    true
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.alert -> {
                    loadFragment(AlertsFragment())
                    true
                }

                else -> {loadFragment(HomeFragment())
                    true}
            }
        }
        SharedPreference.getInstance(this).setNotification("enable")

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView,fragment)
        transaction.commit()
    }

}