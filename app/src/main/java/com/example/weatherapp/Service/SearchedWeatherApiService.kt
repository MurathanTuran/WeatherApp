package com.example.weatherapp.Service

import com.example.weatherapp.Model.Model
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchedWeatherApiService {

    @GET("/data/2.5/weather")
    suspend fun getSearchedLocationData(@Query("q") city: String, @Query("appid") appid: String, @Query("units") units: String): Response<Model>

}