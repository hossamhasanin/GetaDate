package com.hossam.hasanin.getadate.ViewModels.MainPage

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.hossam.hasanin.getadate.Externals.*
import com.hossam.hasanin.getadate.Models.User
import kotlinx.coroutines.*

class CardsViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val data = MutableLiveData<MutableList<User>>()

    suspend fun getUsersData(lastUsename: String?) : Deferred<QuerySnapshot>{
        if (lastUsename.isNullOrEmpty()){
            return firestore.collection("users")
                .whereEqualTo("gender" , currentUser?.getGenderReverse())
                .orderBy("username").limit(10).get().asDeferred()
        } else {
            return firestore.collection("users")
                .whereEqualTo("gender" , currentUser?.getGenderReverse())
                .orderBy("username").startAfter(lastUsename).limit(10).get().asDeferred()
        }
    }

    fun getUserCharacteristics(userId: String) : Deferred<QuerySnapshot>{
        return firestore.collection("users").document(userId)
            .collection("characteristics").orderBy("order").get().asDeferred()
    }

    suspend fun getUsersDidntSeen(lastUsename: String?) : ArrayList<User>{
        val users = getUsersData(lastUsename).await().toObjects(User::class.java)
        var finalUsers = arrayListOf<User>()
        val currentLikedList = currentUser?.getLikedList()
        val currentDidnotLikedList = currentUser?.getDidnotLikedList()
        val currentLocation = currentUser?.getLocation()
        val cLoc = Location("")
        cLoc.longitude = currentLocation?.get(0)!!
        cLoc.latitude = currentLocation?.get(1)!!

        if (currentLikedList!!.isNotEmpty() || currentDidnotLikedList!!.isNotEmpty()){
            users.forEach {
                val oLoc = Location("")
                oLoc.longitude = it.location?.get(0)!!
                oLoc.latitude = it.location?.get(1)!!

                val calculatedDistance = cLoc.distanceTo(oLoc)

                if (!currentLikedList!!.contains(it.id) &&
                    !currentDidnotLikedList!!.contains(it.id) && calculatedDistance <= Constants.MINIMUM_COMPARISION_DISTANCE){
                    finalUsers.add(it)
                }
            }
            finalUsers.sortWith(Comparator<User> { p0, p1 ->
                val p0Loc = Location("")
                p0Loc.longitude = p0.location?.get(0)!!
                p0Loc.latitude = p0.location?.get(1)!!

                val p1Loc = Location("")
                p1Loc.longitude = p1.location?.get(0)!!
                p1Loc.latitude = p1.location?.get(1)!!

                val p0Distance = cLoc.distanceTo(p0Loc)
                val p1Distance = cLoc.distanceTo(p1Loc)


                if (p0Distance < p1Distance){
                    return@Comparator 0
                } else if (p0Distance == p1Distance){
                    return@Comparator 1
                } else {
                    return@Comparator -1
                }
            })
        } else {
            finalUsers = users as ArrayList<User>
        }

        return finalUsers
    }

    suspend fun setLikedUser(id: String?) : Deferred<Void>{
        val userlikedList : ArrayList<String?> = currentUser?.getLikedList()!!
        userlikedList.add(id)
        val data = hashMapOf("liked" to userlikedList)
        firestore.collection("users").document(id!!).get().addOnSuccessListener {
            val likedList = it.data?.getOrElse("liked" , {arrayListOf<String>()}) as ArrayList<String>
            val matchList = it.data?.getOrElse("match" , { arrayListOf<String>()}) as ArrayList<String>
            if (likedList.contains(currentUser.uid)){
                matchList.add(currentUser.uid)
                var matchData = hashMapOf("match" to matchList)
                CoroutineScope(Dispatchers.IO).launch {
                    firestore.collection("users").document(id).update(matchData as Map<String , Any>).asDeferred().await()
                    val currentUserMatchs = currentUser.getMatchList()
                    if (!currentUserMatchs.contains(id)){
                        currentUserMatchs.add(id)
                        matchData = hashMapOf("match" to currentUserMatchs)
                        firestore.collection("users").document(currentUser.uid).update(matchData as Map<String , Any>).asDeferred().await()
                    }
                }
            }
        }
        return firestore.collection("users").document(currentUser.uid).update(data as Map<String, Any>).asDeferred()
    }

    suspend fun setDisLikedUser(id: String?) : Deferred<Void>{
        val userDislikedList = currentUser?.getLikedList()!!
        userDislikedList.add(id)
        val data = hashMapOf("disliked" to userDislikedList)
        return firestore.collection("users").document(currentUser.uid).update(data as Map<String, Any>).asDeferred()
    }

    suspend fun getCharQuesNum() : Int?{
        return firestore.getCharQuesNum()
    }


}
