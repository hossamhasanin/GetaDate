package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.hossam.hasanin.getadate.Externals.*
import com.hossam.hasanin.getadate.Models.User

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.LoginActivity
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.ProfileFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.ProfileViewModel
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseMainPageFragment() , KodeinAware {

    override val kodein by closestKodein()

    private val profileFactory:ProfileFactory by instance()

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.toolbar!!.visibility = View.VISIBLE

        viewModel = ViewModelProviders.of(this , profileFactory).get(ProfileViewModel::class.java)


        bindUi()


        edit_characteristics.setOnClickListener {
            val action = ProfileFragmentDirections.addEditQuestions()
            action.edit = true
            Navigation.findNavController(it).navigate(action)
        }

        add_more_questions.setOnClickListener {
            val action = ProfileFragmentDirections.addEditQuestions()
            Navigation.findNavController(it).navigate(action)
        }


    }


    private fun bindUi() = launch {

        val username = viewModel.currentUser!!.displayName
        text_cont1.text = username
        val age = viewModel.currentUser!!.getAge()
        text_cont2.text = getString(R.string.parameter_age , age.toString())
    }


}
