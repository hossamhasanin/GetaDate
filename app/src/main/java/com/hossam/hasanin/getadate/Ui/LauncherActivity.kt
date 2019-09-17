package com.hossam.hasanin.getadate.Ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.Factories.LauncherFactory
import com.hossam.hasanin.getadate.ViewModels.LauncherViewModel
import com.rbddevs.splashy.Splashy
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class LauncherActivity : AppCompatActivity() , KodeinAware {

    override val kodein by closestKodein()

    private val launcherFactory : LauncherFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val viewModel = ViewModelProviders.of(this , launcherFactory).get(LauncherViewModel::class.java)

//        viewModel.login.observe(this , Observer {
//            Splashy.hide()
//        })

        setSplashScreen()

        viewModel.checkLoggedIn {
            Splashy.hide()
        }

        Splashy.onComplete(object : Splashy.OnComplete{
            override fun onComplete() {
                if (viewModel.login){
                    startActivity(Intent(this@LauncherActivity , MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@LauncherActivity , LoginActivity::class.java))
                    finish()
                }
                Log.v("koko" , "act")

                overridePendingTransition(android.R.anim.slide_in_left , android.R.anim.slide_out_right)
            }
        })
    }

    fun setSplashScreen(){
        Splashy(this).setLogo(R.drawable.main_logo)
            .setBackgroundResource(R.drawable.launcer_background)
            .setTitle(R.string.app_name)
            .setTitleColor(android.R.color.white)
            .setTitleSize(30F)
            .setSubTitle(R.string.sub_title_app)
            .setSubTitleColor(android.R.color.white)
            .setSubTitleSize(20F)
            .setAnimation(Splashy.Animation.SLIDE_IN_TOP_BOTTOM)
            .setInfiniteDuration(true)
            .show()
    }

}
