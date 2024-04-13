package com.example.weatherforecast.home.view


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
import com.example.weatherforecast.R
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.helpers.checkLanguage
import com.example.weatherforecast.helpers.checkNetworkConnection
import com.example.weatherforecast.helpers.getLocationName
import com.example.weatherforecast.helpers.getDate
import com.example.weatherforecast.helpers.getHour
import com.example.weatherforecast.helpers.setIcon
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.CurrentState
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.model.entity.HomeEntity
import com.example.weatherforecast.network.RemoteDataSourceImpl
import com.example.weatherforecast.settings.viewmodel.SettingsViewModel
import com.example.weatherforecast.settings.viewmodel.SettingsViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    val PERMISSION_ID = 1010
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var settingsViewModel : SettingsViewModel
    private lateinit var settingsFactory: SettingsViewModelFactory
    private lateinit var homeFactory : HomeViewModelFactory
    private lateinit var  sharedPreference: SharedPreference
    private lateinit var entityHome: HomeEntity
    private val TAG = "HomeFragment"

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
        initViewModel()
        setUpDayRecyclerView()
        setUpHourRecyclerView()

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

    private fun initViewModel(){
        homeFactory=HomeViewModelFactory(
            RepositoryImpl.getInstance(
                RemoteDataSourceImpl.getInstance(), LocalDataSourceImp(requireContext()))
        )

        homeViewModel = ViewModelProvider(this,homeFactory)[HomeViewModel::class.java]

        settingsFactory=SettingsViewModelFactory(SharedPreference.getInstance(requireContext()))


        settingsViewModel =
            ViewModelProvider(this, settingsFactory)[SettingsViewModel::class.java]
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
                    Log.d(TAG, "ahmed before" + SharedPreference.getInstance(requireContext()).getLatHome() + SharedPreference.getInstance(requireContext()).getLonHome())
                    SharedPreference.getInstance(requireContext()).setLatAndLonHome(location?.latitude!!,location?.longitude!!)
                    Log.d(TAG, "ahmed after" + SharedPreference.getInstance(requireContext()).getLatHome() + SharedPreference.getInstance(requireContext()).getLonHome())
                    if (checkNetworkConnection(requireContext())){
                        getHomeWeather(location?.latitude!!,location?.longitude!!)
                    }else{
                        getWeatherFromDB()
                    }

                    fusedLocationProviderClient.removeLocationUpdates(this);
                }
            },
            Looper.myLooper()
        )
    }


    fun getHomeWeather(lat: Double?, lon: Double?){
        var language : String? = null
        settingsViewModel.changeLanguageShared("ar")
        lifecycleScope.launch {
            settingsViewModel.language.collect(){
                language = it
                Log.d(TAG, "sharedflow language " + language)
            }
        }

        if (SharedPreference.getInstance(requireContext()).getMap() == "Home") {
            homeViewModel.getWeather(SharedPreference.getInstance(requireContext()).getLat(),
                SharedPreference.getInstance(requireContext()).getLon(),
                SharedPreference.getInstance(requireContext()).getUnit(),
                SharedPreference.getInstance(requireContext()).getLanguage())
        }else{
            homeViewModel.getWeather(SharedPreference.getInstance(requireContext()).getLatHome(),
                SharedPreference.getInstance(requireContext()).getLonHome(),
                SharedPreference.getInstance(requireContext()).getUnit(),
                SharedPreference.getInstance(requireContext()).getLanguage())
        }

        lifecycleScope.launch {
            homeViewModel.weather.collectLatest { result ->
                when(result){
                    is ApiState.Loading ->{
                        loading()
                    }

                    is ApiState.Success ->{
                        success()
                        initUI(result.weather)
                        //homeViewModel.deleteCurrentWeather()
                        insertCurrentToDB(result.weather)
                        Log.d(TAG, "refat showDialog: delay" + entityHome)
                        homeViewModel.insertCurrentWeather(entityHome)
                        hourAdapter.submitList(result.weather.hourly)
                        dayAdapter.submitList(result.weather.daily)
                    }

                    else ->{
                    }
                }
            }
        }
    }

    private fun insertCurrentToDB(weather: WeatherResponse){
        homeViewModel.deleteCurrentWeather()
        entityHome = HomeEntity()
        entityHome.lat = weather.lat
        Log.d(TAG, "refat showDialog: weather.lat" + weather.lat)
        entityHome.lon = weather.lon
        Log.d(TAG, "refat showDialog: weather.lon" + weather.lon)
        entityHome.current = weather.current
        Log.d(TAG, "refat showDialog:weather.current" + weather.current)
        entityHome.hourly = weather.hourly
        Log.d(TAG, "refat showDialog: weather.hourly" + weather.hourly)
        entityHome.daily = weather.daily
        Log.d(TAG, "refat showDialog: weather.daily" + weather.daily)
        entityHome.current!!.weather[0].icon = weather.current.weather[0].icon


    }

    fun getWeatherFromDB(){
        homeViewModel.getCurrentWeather()
        lifecycleScope.launch {
            homeViewModel.current.collectLatest {result ->
                when(result){
                    is CurrentState.Loading ->{
                        loading()
                    }
                    is CurrentState.Success ->{

                        success()
                        loadUI(result.homeEntity)
                        hourAdapter.submitList(result.homeEntity.hourly)
                        dayAdapter.submitList(result.homeEntity.daily)
                    }
                    else ->{

                    }
                }
            }
        }
    }

    private fun initUI(weather : WeatherResponse){
        setWeatherUnit()
        var locale = checkLanguage(requireContext())
        val numberFormat = NumberFormat.getInstance(locale)
        var weatherUnit = setWeatherUnit()
        binding.tvCurrentAddress.text = getLocationName(requireActivity(), weather.lat, weather.lon)
        binding.tvDate.text = getDate(requireActivity(),weather.current.dt)
        binding.tvTemp.text = numberFormat.format(weather.current.temp.toInt()).toString()
        binding.tvDescription.text  = weather.current.weather[0].description
        setIcon(weather.current.weather[0].icon, binding.ivIcon)
//        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.current.weather[0].icon+"@4x.png")
//            .into(binding.ivIcon)
        binding.tvPressure.text = numberFormat.format(weather.current.pressure).toString()+getString(R.string.pressure_unit)
        binding.tvHumidity.text = numberFormat.format(weather.current.humidity).toString()+" %"
        binding.windSpeedValue.text = numberFormat.format(weather.current.wind_speed).toString()+" "+weatherUnit
        binding.windDegreeValue.text = numberFormat.format(weather.current.wind_deg).toString()+ " °"
        //binding.tvHumidity.text = weather.current.clouds.toString()
        binding.tvUltraviolet.text =numberFormat.format(weather.current.uvi).toString()+" %"
        binding.tvVisibility.text = numberFormat.format(weather.current.visibility).toString()+getString(R.string.visibility_unit)
        binding.progressBarValue.text = numberFormat.format(weather.current.humidity).toString()+" %"
        binding.feelsLikeValue.text = numberFormat.format(weather.current.feels_like).toString() +" " + binding.tvTempUnit.text
        binding.tvSunrise.text = getHour(requireContext(),weather.current.sunrise)
        binding.tvSunset.text = getHour(requireContext(),weather.current.sunset)

    }

    private fun loadUI(weather: HomeEntity){

        binding.tvTempUnit.text = " °"
        binding.tvCurrentAddress.text = getLocationName(requireActivity(), weather.lat, weather.lon)
        binding.tvDate.text = getDate(requireActivity(),weather.current!!.dt)
        binding.tvTemp.text = weather.current!!.temp.toInt().toString()
        binding.tvDescription.text  = weather.current!!.weather[0].description
        setIcon(weather.current!!.weather[0].icon, binding.ivIcon)
//        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.current!!.weather[0].icon+"@4x.png")
//            .into(binding.ivIcon)
        binding.tvPressure.text = weather.current!!.pressure.toString()
        binding.tvHumidity.text = weather.current!!.humidity.toString()
        binding.windSpeedValue.text = weather.current!!.wind_speed.toString()
        binding.windDegreeValue.text = weather.current!!.wind_deg.toString()
        //binding.tvHumidity.text = weather.current!!.clouds.toString()
        binding.tvUltraviolet.text =weather.current!!.uvi.toString()
        binding.tvVisibility.text = weather.current!!.visibility.toString()
        binding.progressBarValue.text = weather.current!!.humidity.toString()
        binding.feelsLikeValue.text = weather.current!!.feels_like.toString()+ binding.tvTempUnit.text
        binding.tvSunrise.text = getHour(requireContext(),weather.current!!.sunrise)
        binding.tvSunset.text = getHour(requireContext(),weather.current!!.sunset)
    }

    fun success(){
        binding.loading.visibility = View.GONE
        binding.currentTempLayout.visibility = View.VISIBLE
        binding.rvHourly.visibility = View.VISIBLE
        binding.rvDaily.visibility = View.VISIBLE
        binding.detailsLayout.visibility = View.VISIBLE
        binding.windLayout.visibility = View.VISIBLE
        binding.tvDaily.visibility = View.VISIBLE
        binding.tvHourly.visibility =View.VISIBLE
    }

    fun loading(){
        binding.loading.visibility = View.VISIBLE
        binding.currentTempLayout.visibility = View.GONE
        binding.rvHourly.visibility = View.GONE
        binding.rvDaily.visibility = View.GONE
        binding.detailsLayout.visibility = View.GONE
        binding.windLayout.visibility = View.GONE
        binding.tvDaily.visibility = View.GONE
        binding.tvHourly.visibility =View.GONE
    }

    private fun setUpDayRecyclerView() {
        dayLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
        dayAdapter = DayAdapter(requireContext())
        binding.rvDaily.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }
    }

    private fun setUpHourRecyclerView() {
        hourLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.HORIZONTAL, false)
        hourAdapter = HourAdapter(requireContext())
        binding.rvHourly.apply {
            adapter = hourAdapter
            layoutManager = hourLayoutManager
        }
    }

    fun setWeatherUnit(): String {
        var unit : String
        if (settingsViewModel.getTemperatureUnit() == "metric") {
            binding.tvTempUnit.text = getString(R.string.celsius_unit)
            unit = "mph" + getString(R.string.meter_hour)
        }

        else if (settingsViewModel.getTemperatureUnit() == "default") {
            binding.tvTempUnit.text = getString(R.string.kelvin_unit)
            unit = getString(R.string.meter_hour)
        }else {
            binding.tvTempUnit.text = getString(R.string.frehirnit_unit)
            unit = getString(R.string.mile_second)
        }
        return unit


    }


}