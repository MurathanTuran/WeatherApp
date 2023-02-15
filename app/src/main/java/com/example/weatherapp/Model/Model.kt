package com.example.weatherapp.Model

import com.google.gson.annotations.SerializedName

data class Model(
    @SerializedName("weather")
    val weather: List<WeatherModel>,
    @SerializedName("main")
    val main: MainModel,
    @SerializedName("name")
    val cityName: String

)
