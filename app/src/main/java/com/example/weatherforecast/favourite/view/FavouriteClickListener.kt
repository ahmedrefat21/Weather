package com.example.weatherforecast.favourite.view

import com.example.weatherforecast.model.entity.FavoriteEntity


interface FavouriteClickListener {
    fun showFavouriteDetails(favoriteEntity: FavoriteEntity)
    fun deleteFavourite(id : Int)
}