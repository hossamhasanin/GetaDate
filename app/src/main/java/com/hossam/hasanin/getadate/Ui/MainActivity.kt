package com.hossam.hasanin.getadate.Ui

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    var currentPage = MainPages.CARDS
    val themeListen = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        CoroutineScope(Dispatchers.Main).launch {
            val mAuth = FirebaseAuth.getInstance()
            if (mAuth.currentUser!!.getGender() == 1){
                setTheme(R.style.MaleMode)
                toolbar.setBackgroundResource(R.drawable.toolbar2)
                setStatusBarColor(R.color.purpule)
            } else {
                setTheme(R.style.FeMaleMode)
                toolbar.setBackgroundResource(R.drawable.toolbar_bink_unified)
                setStatusBarColor(R.color.dark_bink)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        right_icon.setOnClickListener {
            if (currentPage == MainPages.CARDS){
                currentPage = MainPages.MATCHES
                this.findNavController(R.id.nav_host_fragment).navigate(R.id.go_to_match_list)
            }
        }

        left_icon.setOnClickListener {
            if (currentPage == MainPages.CARDS){
                currentPage = MainPages.PROFILE
                this.findNavController(R.id.nav_host_fragment).navigate(R.id.go_to_profile)
            } else {
//                if (currentPage == MainPages.SHOW_USER){
//                    currentPage = MainPages.MATCHES
//                } else if (currentPage == MainPages.RESERVE_RESTURANT || currentPage == MainPages.PICK_TIME) {
//                    currentPage = MainPages.SHOW_USER
//                } else if (currentPage == MainPages.PROFILE) {
//                    currentPage = MainPages.CARDS
//                } else if (currentPage == MainPages.MATCHES){
//                    currentPage = MainPages.CARDS
//                }
                this.findNavController(R.id.nav_host_fragment).navigateUp()
            }
        }
    }

    private fun setStatusBarColor(color : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this@MainActivity , color)
        }
    }


}

enum class MainPages{
    CARDS , PROFILE , MATCHES , SHOW_USER , RESERVE_RESTURANT
    , PICK_TIME , DETAILS , ADVICES , EDIT_CHARACTERISTICS
}
