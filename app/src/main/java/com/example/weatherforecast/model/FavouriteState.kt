package com.example.weatherforecast.model

import com.example.weatherforecast.model.entity.FavoriteEntity


sealed class FavouriteState {
    class Success(val
                  data: List<FavoriteEntity>): FavouriteState()
    class Failure(val msg:Throwable): FavouriteState()
    object Loading: FavouriteState()
}