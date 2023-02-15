package com.example.weatherapp.Model

import com.google.gson.annotations.SerializedName

data class WeatherModel(
    @SerializedName("main")
    val main: String,
    @SerializedName("icon")
    val weatherIcon: String

)
