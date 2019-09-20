package com.hossam.hasanin.getadate.Externals

import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData


object LocationHandeler {

    var mlocation: Location? = null
    var gpsIsDisabled = MutableLiveData<Boolean>()
    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mlocation = location
            Log.d("Location Changes", location.toString())
        }

        override fun onStatusChanged(
            provider: String?,
            status: Int,
            extras: Bundle?
        ) {
            Log.d("Status Changed", status.toString())
        }

        override fun onProviderEnabled(provider: String?) {
            Log.d("Provider Enabled", provider)
            gpsIsDisabled.postValue(false)
        }

        override fun onProviderDisabled(provider: String?) {
            Log.d("Provider Disabled", provider)
            gpsIsDisabled.postValue(true)
        }
    }




}