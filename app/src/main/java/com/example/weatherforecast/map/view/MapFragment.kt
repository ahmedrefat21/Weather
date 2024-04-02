package com.example.weatherforecast.map.view

import android.location.Address
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.map.viewmodel.MapViewModelFactory
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentMapBinding
import com.example.weatherforecast.favourite.view.FavouriteFragment
import com.example.weatherforecast.helpers.getLocationName

import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.map.viewmodel.MapViewModel
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.network.RemoteDataSourceImpl

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() ,OnMapReadyCallback {

    lateinit var map : GoogleMap
    private var marker: Marker? = null
    lateinit var binding: FragmentMapBinding
    private lateinit var viewModel : MapViewModel
    private lateinit var mapFactory : MapViewModelFactory
    private var address : Address? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initViewModel()
        saveLocation()
    }

    override fun onMapReady(googleMap : GoogleMap) {
        map = googleMap
        setPoiClick(map)
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker?.showInfoWindow()
            binding.locationLayout.visibility = View.VISIBLE
            if (marker != null)
                binding.tvCity.text = getLocationName(requireContext(),marker!!.position.latitude,marker!!.position.longitude)

            binding.locationLayout.visibility = View.VISIBLE
        }
    }



    private fun initViewModel(){
        mapFactory= MapViewModelFactory(
            RepositoryImpl.getInstance(
                RemoteDataSourceImpl.getInstance(), LocalDataSourceImp(requireContext())
            ))
        viewModel = ViewModelProvider(this,mapFactory)[MapViewModel::class.java]
    }

    private fun saveLocation(){
        binding.btnSaveLocation.setOnClickListener {

            if (marker != null){
                val favoriteEntity = FavoriteEntity(latitude = marker!!.position.latitude, longitude = marker!!.position.longitude, address = getLocationName(requireContext(),marker!!.position.latitude,marker!!.position.longitude))
                viewModel.insertFavoriteCity(favoriteEntity)
            }else{
                val favoriteEntity = FavoriteEntity(latitude = address!!.latitude, longitude = address!!.longitude, address = getLocationName(requireContext(),address!!.latitude,address!!.longitude))
                viewModel.insertFavoriteCity(favoriteEntity)
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FavouriteFragment()).commit()

        }
    }
}