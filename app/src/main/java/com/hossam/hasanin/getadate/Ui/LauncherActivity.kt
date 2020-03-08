package com.hossam.hasanin.getadate.Ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.Factories.LauncherFactory
import com.hossam.hasanin.getadate.ViewModels.LauncherViewModel
import com.rbddevs.splashy.Splashy
import kotlinx.android.synthetic.main.activity_launcher.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class LauncherActivity : AppCompatActivity() , KodeinAware {

    override val kodein by closestKodein()

    private val launcherFactory : LauncherFactory by instance()

    private lateinit var animationDrawable : AnimationDrawable;

    private lateinit var viewModel:LauncherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        viewModel = ViewModelProviders.of(this , launcherFactory).get(LauncherViewModel::class.java)

        buildBluryAnimation()
        expandWhiteBox()

        viewModel.login.observe(this , Observer {loggedIn ->
            if (loggedIn){
                startActivity(Intent(this@LauncherActivity , MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@LauncherActivity , LoginActivity::class.java))
                finish()
            }

        })


    }

    private fun buildBluryAnimation(){
        animationDrawable = AnimationDrawable()
        animationDrawable.addFrame(ContextCompat.getDrawable(this , R.drawable.normal_background)!! , 1500)
        animationDrawable.addFrame(ContextCompat.getDrawable(this , R.drawable.blury_background)!! , 1000)
        animationDrawable.isOneShot = true
        background.setImageDrawable(animationDrawable)
        animationDrawable.start()
    }

    private fun expandWhiteBox(){
        val targetHeight = 200
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                center_white.layoutParams.height =
                    if (interpolatedTime == 1f) targetHeight else (targetHeight * interpolatedTime).toInt()
                center_white.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = 1000
        a.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                showLogo()
            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })
        center_white.startAnimation(a)
    }

    private fun showLogo(){
        val targetAlpha = 1f
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                logo.alpha =
                    if (interpolatedTime == 1f) targetAlpha else targetAlpha * interpolatedTime
                logo.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = 1000
        a.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                viewModel.checkLoggedIn()
            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })
        logo.startAnimation(a)
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
