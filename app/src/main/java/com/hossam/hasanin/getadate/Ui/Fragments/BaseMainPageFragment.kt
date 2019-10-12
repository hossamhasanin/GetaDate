package com.hossam.hasanin.getadate.Ui.Fragments

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.getadate.Externals.checkIfTheUserExsist
import com.hossam.hasanin.getadate.Ui.LoginActivity
import kotlinx.coroutines.launch

open class BaseMainPageFragment : BaseFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            checkIfTheUserExsist {doesUserExist ->
                if (!doesUserExist){
                    FirebaseAuth.getInstance().currentUser!!.delete()
                    startActivity(Intent(this@BaseMainPageFragment.activity , LoginActivity::class.java))
                }
            }
        }
    }
}