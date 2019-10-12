package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.UserCharacteristic

class EnhancePersonalityViewModel : ViewModel() {

    val mAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val characteristics = MutableLiveData<List<UserCharacteristic>?>()

    fun getUserCharacteristics(){
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("characteristics").get().addOnCompleteListener {
                if (it.isSuccessful){
                    val data = it.result!!.toObjects(UserCharacteristic::class.java)
                    var i = 0
                    it.result!!.documents.forEach {u ->
                        data[i].withId(u.id)
                        i += 1
                    }
                    if (data.isNotEmpty()){
                        characteristics.postValue(data)
                    } else {
                        characteristics.postValue(null)
                    }
                } else {
                    characteristics.postValue(null)
                }
            }
    }

}
