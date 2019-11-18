package com.hossam.hasanin.getadate.ViewModels.MainPage

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.getLocation
import com.hossam.hasanin.getadate.Models.DatingRequest
import com.hossam.hasanin.getadate.Models.Resturant

class ReserveResturantViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val completedSuccessFully = MutableLiveData<Boolean>()
    val allResturants = MutableLiveData<List<Resturant>?>()

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

    fun getResturants(userLocation: ArrayList<Double?>){
        firestore.collection("resturants").get().addOnCompleteListener {
            if (it.isSuccessful){
                val resturants = it.result!!.toObjects(Resturant::class.java)
                resturants.sortWith(Comparator<Resturant> { p0, p1 ->
                    val p0Loc = Location("")
                    p0Loc.longitude = p0.location?.get(0)!!
                    p0Loc.latitude = p0.location?.get(1)!!

                    val p1Loc = Location("")
                    p1Loc.longitude = p1.location?.get(0)!!
                    p1Loc.latitude = p1.location?.get(1)!!

                    val userCorrdinates = Location("")
                    userCorrdinates.longitude = userLocation[0]!!
                    userCorrdinates.latitude = userLocation[1]!!

                    val p0Distance = userCorrdinates.distanceTo(p0Loc)
                    val p1Distance = userCorrdinates.distanceTo(p1Loc)


                    if (p0Distance < p1Distance){
                        return@Comparator 0
                    } else if (p0Distance == p1Distance){
                        return@Comparator 1
                    } else {
                        return@Comparator -1
                    }
                })

                allResturants.postValue(resturants)
            } else {
                allResturants.postValue(null)
            }
        }
    }

}
