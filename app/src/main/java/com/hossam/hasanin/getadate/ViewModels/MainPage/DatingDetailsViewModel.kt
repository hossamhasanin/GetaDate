package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.DatingRequest
import com.hossam.hasanin.getadate.Models.Resturant

class DatingDetailsViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val resturantListener = MutableLiveData<Resturant?>()
    val requestListener = MutableLiveData<DatingRequest?>()

    fun getRequestDetails(requestId: String){
        firestore.collection("datingRequests")
            .document(requestId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null){
                    if (documentSnapshot!!.exists()){
                        val resturantId = documentSnapshot.data?.getOrElse("place" , {null}) as String
                        getPlaceInfo(resturantId)
                        val request = documentSnapshot.toObject(DatingRequest::class.java)
                        request!!.withId(documentSnapshot.id)
                        requestListener.postValue(request)
                    } else {
                        requestListener.postValue(null)
                    }
                }
            }
    }

    private fun getPlaceInfo(resturantId:String?){
        if (resturantId != null){
            firestore.collection("resturants")
                .document(resturantId)
                .get().addOnCompleteListener { resturantDocument ->
                    if (resturantDocument.isSuccessful){
                        val resturant = resturantDocument.result?.toObject(Resturant::class.java)
                        resturantListener.postValue(resturant)
                    } else {
                        resturantListener.postValue(null)
                    }
                }
        }
    }

    fun refuseThePlace(requestId: String){
        firestore.collection("datingRequests")
            .document(requestId)
            .update("placeAccepted" , false)
    }

    fun refuseTheTime(requestId: String){
        firestore.collection("datingRequests")
            .document(requestId)
            .update("timeAccepted" , false)
    }


}
