package com.core.pin_location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val PERMISSION_REQUEST_LOCATION = 101
    var currentLocation : String = "CURRENT_LOCATION"
    var nearbyPlaces : String = "NEARBY_PLACES"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cLocationButton.setOnClickListener {
            checkLocationPermission(currentLocation);
        }
        nearbyPlacesButton.setOnClickListener {
            checkLocationPermission(nearbyPlaces);
        }
    }

    fun checkLocationPermission(screenName:String) {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
            } else {
                onPermissionGranted(PERMISSION_REQUEST_LOCATION, screenName)
            }
        } catch (e: Exception) {
            Log.e("", e.localizedMessage.toString())
        }
    }


    fun onPermissionGranted(requestCode: Int, screenName:String) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {

           if(screenName == currentLocation)
           {
               val intent = Intent(applicationContext, MapActivity::class.java)
               startActivity(intent)
           }
           else
           {
               val intent = Intent(applicationContext, AdminFilterMapActivity::class.java)
               startActivity(intent)
           }
        }
    }

    fun onPermissionDenied(requestCode: Int) {

    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION
        )
    }
}