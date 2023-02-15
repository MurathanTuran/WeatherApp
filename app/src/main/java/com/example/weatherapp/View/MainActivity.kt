package com.example.weatherapp.View

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.SingletonLocation
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        getLocation()

        bottomNavigationClick()

    }



    private fun registerLauncher(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                        if(lastLocation!=null){
                            SingletonLocation.location = lastLocation
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }

            }
            else{
                Toast.makeText(applicationContext, "Permission Needed", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getLocation(){
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root, "Permission Needed", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }).show()
            }
            else{
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        else{
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken(){
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                override fun isCancellationRequested() = false
            }).addOnSuccessListener { currentLocation: Location? ->
                if(currentLocation != null){
                    SingletonLocation.location = currentLocation
                }
                else{
                    Toast.makeText(applicationContext, "Cannot get location.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView,fragment)
        transaction.commit()
    }

    private fun bottomNavigationClick(){
        val bottomNav =  binding.bottomNavigationView

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.weatherFragment -> { loadFragment(LocationWeatherFragment()) }
                R.id.searchFragment -> { loadFragment(SearchWeatherFragment()) }
            }
            true
        }
    }



}
