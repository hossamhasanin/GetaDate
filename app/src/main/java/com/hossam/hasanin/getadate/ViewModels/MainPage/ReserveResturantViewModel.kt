package com.hossam.hasanin.getadate.ViewModels.MainPage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.DatingRequest

class ReserveResturantViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val completedSuccessFully = MutableLiveData<Boolean>()

    fun saveDatingRequestPlaceDetail(requestId : String? , userId: String? , placeId: String?){
        val requestsCollection = firestore.collection("datingRequests")
        if (requestId == null){
            val data = DatingRequest()
            data.to = userId
            data.from = currentUser!!.uid
            data.status = 0
            data.place = placeId
            data.uids = arrayListOf("${currentUser.uid}-$userId" , "$userId-${currentUser.uid}")
            requestsCollection.add(data).addOnCompleteListener {
                if (it.isSuccessful){
                    completedSuccessFully.postValue(true)
                } else {
                    completedSuccessFully.postValue(false)
                }
            }
        } else {
            val data = hashMapOf<String , Any>("place" to placeId!! , "status" to 1 , "placeAccepted" to true)
            requestsCollection.document(requestId).update(data).addOnCompleteListener {
                if (it.isSuccessful){
                    completedSuccessFully.postValue(true)
                } else {
                    completedSuccessFully.postValue(false)
                }
            }
        }
        Log.v("koko" , requestId ?: "nope")
    }

}
