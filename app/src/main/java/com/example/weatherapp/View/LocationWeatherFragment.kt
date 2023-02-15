package com.example.weatherapp.View


import com.example.weatherapp.Service.WeatherApiService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.SingletonLocation
import com.example.weatherapp.databinding.FragmentLocationWeatherBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.ceil


class LocationWeatherFragment : Fragment() {

    private lateinit var binding: FragmentLocationWeatherBinding

    private val BASE_URL = "https://api.openweathermap.org/"

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler({coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun loadData(){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)



        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = retrofit.getCurrentLocationData(SingletonLocation.location?.latitude.toString(), SingletonLocation.location?.longitude.toString(), "YOUR_API_KEY", "metric")

            withContext(Dispatchers.Main){

                if(response.isSuccessful){
                    response.body()?.let {
                        binding.cityNameTextLocationWeatherFragment.text = it.cityName
                        Picasso.get().load("https://openweathermap.org/img/wn/${it.weather.get(0).weatherIcon}@2x.png").resize(500, 500).into(binding.weatherImageViewLocationWeatherFragment)
                        binding.weatherTextLocationWeatherFragment.text = it.weather.get(0).main
                        binding.temperatureTextLocationWeatherFragment.text = "${ceil(it.main.temperature.toDouble()).toInt()}°C"
                    }
                }


            }
        }

    }





}