package com.example.weatherforecast.settings.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.map.view.MapFragment
import com.example.weatherforecast.settings.viewmodel.SettingsViewModel
import com.example.weatherforecast.settings.viewmodel.SettingsViewModelFactory

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsViewModelFactory: SettingsViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        activity?.actionBar?.hide()
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intiViewModel()

        getValueOFTempUnit()
        getValueOFWindSpeed()
        getValueOFLanguage()
        getValueOFNotification()
        getValueOfLocationWay()

        saveTempUnit()
        saveNotification()
        saveLanguage()
        saveWindSpeed()
        saveLocationWay()


    }

    private fun intiViewModel() {
        settingsViewModelFactory =
            SettingsViewModelFactory(SharedPreference.getInstance(requireContext()))

        settingsViewModel =
            ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]
    }

    private fun changeLanguageLocaleTo(lan: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lan)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun getValueOFLanguage(){
        if (settingsViewModel.getLanguage() == "arabic")
            binding.btnArabic.isChecked = true
        else
            binding.btnEnglish.isChecked = true
    }

    private fun getValueOFNotification() {
        if (settingsViewModel.getNotification() == "enable")
            binding.btnEnableNotification.isChecked = true
        else
            binding.btnDisableNotification.isChecked = true

    }

    private fun getValueOFWindSpeed() {
        if (settingsViewModel.getWindSpeed() == "metric")
            binding.btnMeter.isChecked = true
        else
            binding.btnMile.isChecked = true

    }

    private fun getValueOFTempUnit() {
        if (settingsViewModel.getTemperatureUnit() == "metric")
            binding.btnCelsius.isChecked = true
        else if (settingsViewModel.getTemperatureUnit() == "default")
            binding.btnKelvin.isChecked = true
        else
            binding.btnFahrenheit.isChecked = true

    }

    private fun getValueOfLocationWay(){
        if (settingsViewModel.getSavedLocationWay()=="GPS"){
            binding.btnGps.isChecked = true
        }else{
            binding.btnMap.isChecked = true
        }
    }

    private fun saveLanguage() {
        binding.btnArabic.setOnClickListener {
            settingsViewModel.setLanguage("ar")
            settingsViewModel.changeLanguageShared("ar")
            changeLanguageLocaleTo("ar")
        }

        binding.btnEnglish.setOnClickListener {
            settingsViewModel.setLanguage("en")
            settingsViewModel.changeLanguageShared("en")
            changeLanguageLocaleTo("en")
        }
    }

    private fun saveNotification(){
        binding.btnEnableNotification.setOnClickListener {
            settingsViewModel.setNotification("enable")
        }

        binding.btnDisableNotification.setOnClickListener {
            settingsViewModel.setNotification("disable")
        }
    }

    private fun saveWindSpeed() {
        binding.btnMeter.setOnClickListener {
            binding.btnCelsius.isChecked=true
            homeBinding.tvTempUnit.text = " C"
            settingsViewModel.setTemperatureUnit("metric")
            settingsViewModel.setWindSpeed("metric")
        }

        binding.btnMile.setOnClickListener {
            binding.btnFahrenheit.isChecked=true
            homeBinding.tvTempUnit.text = " F"
            settingsViewModel.setTemperatureUnit("imperial")
            settingsViewModel.setWindSpeed("imperial")
        }
    }

    private fun saveTempUnit() {
        binding.btnCelsius.setOnClickListener {
            homeBinding.tvTempUnit.text = " C"
            settingsViewModel.setTemperatureUnit("metric")
            settingsViewModel.setTemperature("metric")
        }
        binding.btnKelvin.setOnClickListener {
            homeBinding.tvTempUnit.text = " K"
            settingsViewModel.setTemperatureUnit("default")
            settingsViewModel.setTemperature("default")
        }

        binding.btnFahrenheit.setOnClickListener {
            homeBinding.tvTempUnit.text = " F"
            settingsViewModel.setTemperatureUnit("imperial")
            settingsViewModel.setTemperature("imperial")
        }
    }

    private fun saveLocationWay(){
        binding.btnGps.setOnClickListener {
            settingsViewModel.setLocationWay("GPS")
            SharedPreference.getInstance(requireContext()).setMap("")
        }

        binding.btnMap.setOnClickListener {
            settingsViewModel.setLocationWay("Map")
            SharedPreference.getInstance(requireContext()).setMap("Home")
//            val intent = Intent(requireActivity(), MapsActivity::class.java)
//            requireActivity().startActivity(intent)

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, MapFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

}