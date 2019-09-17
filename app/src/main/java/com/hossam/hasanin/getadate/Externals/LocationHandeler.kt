package com.hossam.hasanin.getadate.Externals

import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log


object LocationHandeler {

    var mlocation: Location? = null
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
        }

        override fun onProviderDisabled(provider: String?) {
            Log.d("Provider Disabled", provider)
        }
    }




}