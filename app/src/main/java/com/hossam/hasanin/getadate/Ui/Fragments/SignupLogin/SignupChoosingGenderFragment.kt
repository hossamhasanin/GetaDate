package com.hossam.hasanin.getadate.Ui.Fragments.SignupLogin

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.SignupChoosingGenderFactory
import com.hossam.hasanin.getadate.ViewModels.SignupLogin.SignupChoosingGenderViewModel
import kotlinx.android.synthetic.main.signup_choosing_gender_fragment.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SignupChoosingGenderFragment : Fragment() , KodeinAware{

    override val kodein by closestKodein()
    private lateinit var viewModel: SignupChoosingGenderViewModel
    private val signupChoosingGenderFactory:SignupChoosingGenderFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup_choosing_gender_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , signupChoosingGenderFactory).get(SignupChoosingGenderViewModel::class.java)
        male_gender.setOnClickListener {
            goToSignup(1 , it)
        }
        female_gemder.setOnClickListener {
            goToSignup(0 , it)
        }
    }

    private fun goToSignup(gender: Int , view: View){
        val action = SignupChoosingGenderFragmentDirections.goToSignup(gender)
        Navigation.findNavController(view).navigate(action)
    }

}
