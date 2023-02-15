package com.example.weatherapp.Model

import com.google.gson.annotations.SerializedName

data class MainModel(
    @SerializedName("temp")
    val temperature: Float
)
