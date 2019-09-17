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

    var login : Boolean = true
    lateinit var viewModel:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        title_toolbar.text = getString(R.string.login_title)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        val slideIn: Animation  =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_left_to_right)
        val slideOut: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_right_to_left)

        go_to_signup.setOnClickListener {
            if (login){
                goToSignup(slideIn , slideOut)
            }
        }

        go_to_login.setOnClickListener {
            if (!login){
                goToLogin(slideIn , slideOut)
            }
        }

        left_icon.setOnClickListener {
            if (!login){
                goToLogin(slideIn , slideOut)
            }
        }

        loginbtn.setOnClickListener {
            login()
        }

        signupbtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            signup()
        }


    }

    override fun onResume() {
        super.onResume()
        changeTheStatusBarColor()
    }

    fun login(){
        val email: String = email_entry_login.text.toString()
        val password: String = password_entry_login.text.toString()

        viewModel.login(email , password) { user , errors ->
            if (user != null){
                Toast.makeText(applicationContext , getString(R.string.login_done) , Toast.LENGTH_LONG).show()
                startActivity(Intent(this , MainActivity::class.java))
                finish()
            } else {
                showTheErrors(applicationContext , errors , viewModel.ERROR_MESSAGES)
            }
        }

    }

    fun signup(){
        val email: String = email_entry_signup.text.toString()
        val username: String = username_entry_signup.text.toString()
        val password: String = password_entry_signup.text.toString()

        viewModel.signup(email , username , password) { mUser , errors ->
            if (mUser != null){
                Toast.makeText(applicationContext , getString(R.string.signup_done) , Toast.LENGTH_LONG).show()
                startActivity(Intent(this , ProfileSettings::class.java))
                finish()
            } else {
                showTheErrors(applicationContext , errors , viewModel.ERROR_MESSAGES)
            }
        }

    }


    fun changeTheStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(this , R.color.purpule))
        }
    }

    fun goToSignup(slideIn: Animation , slideOut: Animation){
        login = false

        login_layout.startAnimation(slideOut)
        login_layout.visibility = View.GONE

        signup_layout.startAnimation(slideIn)
        signup_layout.visibility = View.VISIBLE
        left_icon.visibility = View.VISIBLE
        title_toolbar.text = getString(R.string.sign_up_title)
    }

    fun goToLogin(slideIn: Animation , slideOut: Animation){
        login = true

        login_layout.startAnimation(slideIn)
        login_layout.visibility = View.VISIBLE

        signup_layout.startAnimation(slideOut)
        signup_layout.visibility = View.GONE
        left_icon.visibility = View.GONE
        title_toolbar.text = getString(R.string.login_title)
    }

}
