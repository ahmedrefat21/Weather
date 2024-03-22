package com.example.weatherforecast.home.view

import WeatherResponse
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.helpers.getAddressEnglish
import com.example.weatherforecast.helpers.getCurrentTime
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.network.RemoteDataSourceImpl
import com.example.weatherproject.Home.view.HourAdapter

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    val PERMISSION_ID = 1010
    private lateinit var viewModel : HomeViewModel
    private lateinit var homeFactory : HomeViewModelFactory
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        homeFactory=HomeViewModelFactory(
            RepositoryImpl.getInstance(
                RemoteDataSourceImpl.getInstance()))
        viewModel = ViewModelProvider(this,homeFactory)[HomeViewModel::class.java]

        dayLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
        dayAdapter = DayAdapter(requireContext())
        binding.rvDaily.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        hourLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.HORIZONTAL, false)
        hourAdapter = HourAdapter(requireContext())
        binding.rvHourly.apply {
            adapter = hourAdapter
            layoutManager = hourLayoutManager
        }


        

    }

    override fun onStart() {
        super.onStart()
        if(checkPermission()){
            if(isLocationEnabled()){
                getFreshLocation()
            }else{
                enableLocationServices()
            }
        }else{
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_ID)
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationServices() {
        Toast.makeText(requireContext(),"Turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if (grantResults.size>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getFreshLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    viewModel.getWeather(location?.latitude!!,location?.longitude!!,"metric","en")
                    lifecycleScope.launch {
                        viewModel.weather.collectLatest { result ->
                            when(result){
                                is ApiState.Loading ->{
                                    Log.i("loading", "Loading: ")
                                    loading()

                                }

                                is ApiState.Success ->{
                                    success()
                                    initUI(result.weather)
                                    hourAdapter.submitList(result.weather.hourly)
                                    dayAdapter.submitList(result.weather.daily)
                                }

                                else ->{
                                    Log.i("Error", "Error: ")
                                }
                            }

                        }
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this);
                }
            },
            Looper.myLooper()
        )
    }

    private fun initUI(weather : WeatherResponse){
        binding.tvCurrentAddress.text = getAddressEnglish(requireActivity(), weather.lat, weather.lon)
        binding.tvDate.text = getCurrentTime(weather.current.dt)
        binding.tvTemp.text = weather.current.temp.toInt().toString()+" °C"
        binding.tvDescription.text  = weather.current.weather[0].description
        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.current.weather[0].icon+"@4x.png")
            .into(binding.ivIcon)
        binding.tvPressure.text = weather.current.pressure.toString()
        //binding.tvHumidity.text = weather.current.humidity.toString()
        binding.windSpeedValue.text = weather.current.wind_speed.toString()
        binding.windDegreeValue.text = weather.current.wind_deg.toString()
        binding.tvHumidity.text = weather.current.clouds.toString()
        binding.tvUltraviolet.text =weather.current.uvi.toString()
        binding.tvVisibility.text = weather.current.visibility.toString()
        binding.progressBarValue.text = weather.current.humidity.toString()
        binding.feelsLikeValue.text = weather.current.feels_like.toString()
        //binding.tvSunrise.text = weather.current.sunrise.toString()
        //binding.tvSunset.text = weather.current.sunset.toString()
    }

    fun success(){
        binding.loading.visibility = View.GONE
        binding.currentTempLayout.visibility = View.VISIBLE
        binding.rvHourly.visibility = View.VISIBLE
        binding.rvDaily.visibility = View.VISIBLE
        binding.detailsLayout.visibility = View.VISIBLE
        binding.windLayout.visibility = View.VISIBLE
    }

    fun loading(){
        binding.loading.visibility = View.VISIBLE
        binding.currentTempLayout.visibility = View.GONE
        binding.rvHourly.visibility = View.GONE
        binding.rvDaily.visibility = View.GONE
        binding.detailsLayout.visibility = View.GONE
        binding.windLayout.visibility = View.GONE
    }



}