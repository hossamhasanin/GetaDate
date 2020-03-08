package com.hossam.hasanin.getadate.Ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hossam.hasanin.getadate.Externals.Constants
import com.hossam.hasanin.getadate.Externals.LocationHandeler
import com.hossam.hasanin.getadate.R
import kotlinx.android.synthetic.main.toolbar.*

const val READING_CODE = 12

class ProfileSettings : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


    }


}
