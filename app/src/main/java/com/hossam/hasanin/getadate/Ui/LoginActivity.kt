package com.hossam.hasanin.getadate.Ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.hossam.hasanin.getadate.Externals.showTheErrors
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login_layout.*
import kotlinx.android.synthetic.main.signup_layout.*
import kotlinx.android.synthetic.main.toolbar.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        title_toolbar.text = getString(R.string.login_title)

    }

    override fun onResume() {
        super.onResume()
        //changeTheStatusBarColor()
    }


    fun changeTheStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(this , R.color.purpule))
        }
    }


}
