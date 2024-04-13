package com.example.weatherforecast.favourite.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.favourite.viewmodel.FavouriteViewModel
import com.example.weatherforecast.favourite.viewmodel.FavouriteViewModelFactory
import com.example.weatherforecast.model.FavouriteState
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavouriteBinding
import com.example.weatherforecast.helpers.checkNetworkConnection
import com.example.weatherforecast.helpers.createDialog
import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.map.view.MapFragment
import com.example.weatherforecast.model.entity.FavoriteEntity
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.network.RemoteDataSourceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() , FavouriteClickListener {


    private lateinit var binding : FragmentFavouriteBinding
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var favouriteViewModel : FavouriteViewModel
    private lateinit var favouriteFactory : FavouriteViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setUpFavouriteRecyclerView()
        getAllFavouriteAreas()
        getFavouriteAreaLocation()
    }


    private fun initViewModel() {
        favouriteFactory= FavouriteViewModelFactory(
            RepositoryImpl.getInstance(
                RemoteDataSourceImpl.getInstance(), LocalDataSourceImp(requireContext())
            ))
        favouriteViewModel = ViewModelProvider(this,favouriteFactory)[FavouriteViewModel::class.java]

    }

    override fun showFavouriteDetails(favoriteEntity: FavoriteEntity) {
        if(checkNetworkConnection(requireContext())){
            val fragment = FavouriteDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable("data", favoriteEntity)
            fragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }else{
            Toast.makeText(requireContext(),"No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteFavourite(id : Int) {
        createDialog(
            title = getString(R.string.title),
            context = requireContext(),
            cancel = {},
            sure = { favouriteViewModel.deleteFavoriteWeather(id) })

    }

    private fun setUpFavouriteRecyclerView() {
        favouriteAdapter = FavouriteAdapter(requireContext(),this@FavouriteFragment)
        binding.rvFavourite.adapter = favouriteAdapter
    }

    private fun getAllFavouriteAreas(){
        favouriteViewModel.getFavoriteWeather()
        lifecycleScope.launch {
            favouriteViewModel.favorite.collectLatest { result ->
                when(result){
                    is FavouriteState.Loading ->{
                        binding.loadingAnimation.visibility= View.VISIBLE
                        binding.rvFavourite.visibility = View.GONE
                        binding.fabAddFavorite.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.GONE
                    }
                    is FavouriteState.Success ->{
                        if (result.data.isNullOrEmpty()){
                            binding.loadingAnimation.visibility= View.GONE
                            binding.layoutEmpty.visibility= View.VISIBLE
                            binding.rvFavourite.visibility = View.GONE
                            binding.fabAddFavorite.visibility = View.VISIBLE
                        }else {
                            delay(1500)
                            binding.loadingAnimation.visibility= View.GONE
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvFavourite.visibility = View.VISIBLE
                            binding.fabAddFavorite.visibility = View.VISIBLE
                            favouriteAdapter.submitList(result.data)
                        }
                    }

                    else ->{
                        Log.i("Error", "Error: ")
                    }
                }
            }
        }
    }

    private fun getFavouriteAreaLocation(){
        binding.fabAddFavorite.setOnClickListener {
            if (checkNetworkConnection(requireContext())) {
                SharedPreference.getInstance(requireContext()).setMap("favorite")
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, MapFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}