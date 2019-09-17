package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.DatingRequest

class PickTimeViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val hasCompletedSuccessfully = MutableLiveData<Boolean>()

    fun saveTheTime(requestId : String? , userId: String? , houre:Int , minute: Int , date: String?){
        if (requestId == null){
            val datingRequest = DatingRequest()
            datingRequest.from = currentUser?.uid
            datingRequest.to = userId
            datingRequest.uids = arrayListOf("${currentUser!!.uid}-$userId" , "$userId-${currentUser.uid}")
            datingRequest.time = "$houre:$minute"
            datingRequest.date = date
            datingRequest.status = 0
            firestore.collection("datingRequests").add(datingRequest).addOnCompleteListener {
                    if (it.isSuccessful){
                        hasCompletedSuccessfully.postValue(true)
                    } else {
                        hasCompletedSuccessfully.postValue(false)
                    }
                }
        } else {
            val data = hashMapOf<String , Any>("time" to "$houre:$minute" , "date" to date!! , "status" to 1 , "timeAccepted" to true)
            firestore.collection("datingRequests")
                .document(requestId).update(data).addOnCompleteListener {
                    if (it.isSuccessful){
                        hasCompletedSuccessfully.postValue(true)
                    } else {
                        hasCompletedSuccessfully.postValue(false)
                    }
                }
        }
    }

}
