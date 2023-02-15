package com.example.weatherapp

import android.location.Location

object SingletonLocation {
    var location: Location? = null

    @JvmName("getLocation1")
    fun getLocation(): Location? {
        return location
    }
}