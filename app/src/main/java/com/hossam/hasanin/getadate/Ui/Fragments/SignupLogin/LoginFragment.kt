package com.hossam.hasanin.getadate.Ui.Fragments.SignupLogin

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.hossam.hasanin.getadate.Externals.showTheErrors

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.LoginFactory
import com.hossam.hasanin.getadate.ViewModels.SignupLogin.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LoginFragment : Fragment() , KodeinAware{

    override val kodein by closestKodein()
    private lateinit var viewModel: LoginViewModel
    private val signupLoginFactory : LoginFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , signupLoginFactory).get(LoginViewModel::class.java)

        loginbtn.setOnClickListener {
            login()
        }

        go_to_signup.setOnClickListener {
            val action = LoginFragmentDirections.signupChoosingGender()
            Navigation.findNavController(it).navigate(action)
        }

        forgot_password.setOnClickListener {
            val email = email_entry_login.text.trim().toString()
            if (email.isEmpty()){
                Toast.makeText(context , getString(R.string.enter_email_before_click_forgot_password) , Toast.LENGTH_LONG).show()
            } else {
                viewModel.forgotPassword(email){
                    if (it){
                        Toast.makeText(context , getString(R.string.done_you_got_your_password) , Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context , getString(R.string.look_something_wrong_happend) , Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    fun login(){
        val email: String = email_entry_login.text.toString()
        val password: String = password_entry_login.text.toString()

        viewModel.login(email , password) { user , errors ->
            if (user != null){
                Toast.makeText(context , getString(R.string.login_done) , Toast.LENGTH_LONG).show()
                val intent = Intent(activity , MainActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("new" , true)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                showTheErrors(activity!!.applicationContext , errors , viewModel.ERROR_MESSAGES)
            }
        }

    }

}
