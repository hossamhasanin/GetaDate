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
    var login = false

    @ExperimentalCoroutinesApi
    fun checkLoggedIn(action: () -> Unit){
        Log.v("koko" , "check")
        if (mAuth.currentUser != null){
            firestore.collection("users").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
                if (it.isSuccessful && it.result!!.exists()){
                    login = true
                } else {
                    login = false
                    mAuth.currentUser!!.delete()
                    Log.v("koko" , it.exception.toString())
                }
                action()
            }
        } else {
            Log.v("koko" , "no user")
            login = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                action()
                this.cancel()
            }
        }
    }

}