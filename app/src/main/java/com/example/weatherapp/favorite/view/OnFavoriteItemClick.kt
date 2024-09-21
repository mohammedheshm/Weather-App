package com.example.weatherapp.favorite.view

import com.example.weatherapp.model.FavoriteWeather

interface OnFavoriteItemClick {

    fun deleteFavorite(favoriteWeather: FavoriteWeather)
    fun showMapDetailsFragment(favoriteId: Int)
}