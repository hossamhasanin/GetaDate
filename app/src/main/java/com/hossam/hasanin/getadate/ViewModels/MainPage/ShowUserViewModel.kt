package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.CharacteristicsNotFound
import com.hossam.hasanin.getadate.Externals.NotFoundUserException
import com.hossam.hasanin.getadate.Models.DatingRequest
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.Models.UserCharacteristic

class ShowUserViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val getUser = MutableLiveData<User>()
    val getUserCharacteristicsList = MutableLiveData<MutableList<UserCharacteristic>>()
    val getDatingRequest = MutableLiveData<DatingRequest?>()
    //val tasksFinished = MutableLiveData<Boolean>()

    fun getUseBasicInfo(id:String){
        firestore.collection("users").document(id).addSnapshotListener{documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null){
                if (documentSnapshot.exists()){
                    val user = documentSnapshot.toObject(User::class.java)
                    getUser.postValue(user)
                } else {
                    throw NotFoundUserException()
                }
            }
        }
    }

    fun getUserChaacteristics(id:String){
        firestore.collection("users").document(id)
            .collection("characteristics").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val userCharacteristics:MutableList<UserCharacteristic> = it.result!!.toObjects(UserCharacteristic::class.java)
                    if (userCharacteristics.isNotEmpty()){
                        getUserCharacteristicsList.postValue(userCharacteristics)
                    } else {
                        throw CharacteristicsNotFound()
                    }
                } else {
                    throw CharacteristicsNotFound()
                }
            }
    }

    fun getUserRequest(id:String){
        firestore.collection("datingRequests")
            .whereArrayContains("uids" , "${currentUser!!.uid}-$id")
            .addSnapshotListener{ document , exception ->
                if (document != null){
                    val requests = document.toObjects(DatingRequest::class.java)
                    if (requests.isNotEmpty()){
                        if (requests.size == 1){
                            requests[0].withId(document.documents[0].id)
                            getDatingRequest.postValue(requests[0])
                        }
                    } else {
                        getDatingRequest.postValue(null)
                    }
                }
            }
    }

    fun deleteTheRequest(requestId: String){
        firestore.collection("datingRequests").document(requestId).delete()
    }

}
