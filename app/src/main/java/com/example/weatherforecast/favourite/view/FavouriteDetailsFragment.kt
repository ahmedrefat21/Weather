package com.example.weatherforecast.favourite.view


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.helpers.getLocationName
import com.example.weatherforecast.helpers.getDate
import com.example.weatherforecast.helpers.getHour
import com.example.weatherforecast.home.view.DayAdapter
import com.example.weatherforecast.home.view.HourAdapter
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavouriteDetailsFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private var latitude : Double? = 0.0
    private var longitude : Double?= 0.0
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var homeFactory : HomeViewModelFactory


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

        val bundle = arguments
        if (bundle != null) {
            val details = bundle.getParcelable<FavoriteEntity>("data")
            longitude = details?.longitude
            latitude = details?.latitude

        }

        homeFactory=HomeViewModelFactory(RepositoryImpl.getInstance(
            RemoteDataSourceImpl.getInstance(),
            LocalDataSourceImp(requireContext())))

        homeViewModel = ViewModelProvider(this,homeFactory)[HomeViewModel::class.java]

        setUpDayRecyclerView()
        setUpHourRecyclerView()
        getFavouriteWeatherDetails()
    }



    private fun initUI(weather : WeatherResponse){
        binding.tvCurrentAddress.text = getLocationName(requireActivity(), weather.lat, weather.lon)
        binding.tvDate.text = getDate(requireActivity(),weather.current!!.dt)
        binding.tvTemp.text = weather.current!!.temp.toInt().toString()+" °C"
        binding.tvDescription.text  = weather.current!!.weather[0].description
        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.current!!.weather[0].icon+"@4x.png")
            .into(binding.ivIcon)
        binding.tvPressure.text = weather.current!!.pressure.toString()
        binding.tvHumidity.text = weather.current!!.humidity.toString()
        binding.windSpeedValue.text = weather.current!!.wind_speed.toString()
        binding.windDegreeValue.text = weather.current!!.wind_deg.toString()
        //binding.tvHumidity.text = weather.current!!.clouds.toString()
        binding.tvUltraviolet.text =weather.current!!.uvi.toString()
        binding.tvVisibility.text = weather.current!!.visibility.toString()
        binding.progressBarValue.text = weather.current!!.humidity.toString()
        binding.feelsLikeValue.text = weather.current!!.feels_like.toString()
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
    }

    fun loading(){
        binding.loading.visibility = View.VISIBLE
        binding.currentTempLayout.visibility = View.GONE
        binding.rvHourly.visibility = View.GONE
        binding.rvDaily.visibility = View.GONE
        binding.detailsLayout.visibility = View.GONE
        binding.windLayout.visibility = View.GONE
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

    private fun getFavouriteWeatherDetails(){
        homeViewModel.getWeather(latitude!!,longitude!!
            , SharedPreference.getInstance(requireContext())
                .getUnit(),
            SharedPreference.getInstance(requireContext())
                .getLanguage())

        lifecycleScope.launch {
            homeViewModel.weather.collectLatest { result ->
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

    }


}