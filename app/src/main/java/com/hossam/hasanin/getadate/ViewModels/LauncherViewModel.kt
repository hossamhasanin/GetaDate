package com.hossam.hasanin.getadate.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rbddevs.splashy.Splashy
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*

class LauncherViewModel : ViewModel() {

    val mAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var login = MutableLiveData<Boolean>()

    fun checkLoggedIn(){
        Log.v("koko" , "check")
        if (mAuth.currentUser != null){
            firestore.collection("users").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
                if (it.isSuccessful && it.result!!.exists()){
                    login.postValue(true)
                } else {
                    login.postValue(false)
                    mAuth.currentUser!!.delete()
                    Log.v("koko" , it.exception.toString())
                }
            }
        } else {
            Log.v("koko" , "no user")
            login.postValue(false)
        }
    }

}