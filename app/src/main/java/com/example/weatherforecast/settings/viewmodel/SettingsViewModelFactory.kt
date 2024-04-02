package com.example.weatherforecast.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.prefernces.SharedPreference

class SettingsViewModelFactory (private val sharedPreference: SharedPreference
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(sharedPreference) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}