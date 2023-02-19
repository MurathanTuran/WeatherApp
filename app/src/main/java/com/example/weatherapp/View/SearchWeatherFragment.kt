package com.example.weatherapp.View

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.weatherapp.Service.SearchedWeatherApiService
import com.example.weatherapp.databinding.FragmentSearchWeatherBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.ceil


class SearchWeatherFragment : Fragment() {

    private lateinit var binding: FragmentSearchWeatherBinding

    private val BASE_URL = "https://api.openweathermap.org/"

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler({coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchWeatherBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchViewSearchWeatherFragment.setIconifiedByDefault(true)
        binding.searchViewSearchWeatherFragment.setSubmitButtonEnabled(true)

        binding.searchViewSearchWeatherFragment.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(requireView().windowToken, 0)
                loadData(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun loadData(query: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchedWeatherApiService::class.java)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = retrofit.getSearchedLocationData(query, "YOUR_API_KEY", "metric")

            withContext(Dispatchers.Main){

                if(response.isSuccessful){
                    response.body()?.let {
                        binding.cityNameTextSearchWeatherFragment.text = it.cityName
                        Picasso.get().load("https://openweathermap.org/img/wn/${it.weather.get(0).weatherIcon}@2x.png").resize(500, 500).into(binding.weatherImageViewSearchWeatherFragment)
                        binding.weatherTextSearchWeatherFragment.text = it.weather.get(0).main
                        binding.temperatureTextSearchWeatherFragment.text = "${ceil(it.main.temperature.toDouble()).toInt()}°C"
                        binding.resultSearchWeatherFragment.visibility = View.VISIBLE
                    }
                }
                else{
                    Toast.makeText(requireContext(), "City not found", Toast.LENGTH_LONG).show()
                }


            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

}