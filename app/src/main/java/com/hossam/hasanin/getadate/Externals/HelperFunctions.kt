package com.hossam.hasanin.getadate.Externals

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.Ui.Fragments.MainPage.CardItem
import com.hossam.hasanin.getadate.Ui.MainPages
import kotlinx.coroutines.*

fun <T> Task<T>.asDeferred(): Deferred<T> {
    var deferred = CompletableDeferred<T>()

    this.addOnSuccessListener {
        deferred.complete(it)
    }

    this.addOnFailureListener{
        deferred.completeExceptionally(it)
    }

    return deferred
}

fun MutableList<UserCharacteristic>.convertToListItems() : List<CardItem> {
    return this.map {
        CardItem(it)
    }
}

fun showTheErrors(context: Context , errors: ArrayList<ErrorTypes> , errorsMessage : HashMap<ErrorTypes , String>){
    var errorMess = ""
    for (error in errors){
        errorMess += errorsMessage.get(error) + " \n"
    }
    Toast.makeText(context , errorMess , Toast.LENGTH_LONG).show()
}

suspend fun FirebaseUser.getFirstName() : String? {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getFirstName.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    return user!!.firstName
}

suspend fun FirebaseUser.getSecondName() : String? {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getSecondName.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    return user!!.secondName
}

suspend fun FirebaseUser.getAddress() : String? {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getAddress.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    return user!!.address
}


suspend fun FirebaseUser.getAge() : Int? {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getAge.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    return user!!.age
}

suspend fun FirebaseUser.getGender() : Int? {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getGender.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    return user!!.gender
}

suspend fun FirebaseUser.getGenderReverse() : Int {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getGenderReverse.uid).get().asDeferred().await()
    val user = data.toObject(User::class.java)
    if (user!!.gender == 1){
        return 0
    }
    return 1
}

suspend fun FirebaseUser.getLikedList() : ArrayList<String?> {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getLikedList.uid).get().asDeferred().await()
    val likedList = data.data?.getOrElse("liked"  , { return arrayListOf() } ) as ArrayList<String?>
    return likedList
}

suspend fun FirebaseUser.getDidnotLikedList() : ArrayList<String?> {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getDidnotLikedList.uid).get().asDeferred().await()
    val dislikedList = data.data?.getOrElse("disliked" , {return arrayListOf()}) as ArrayList<String?>
    return dislikedList
}

suspend fun FirebaseUser.getLocation() : ArrayList<Double?> {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getLocation.uid).get().asDeferred().await()
    val location = data.data?.getOrElse("location" , {return arrayListOf()}) as ArrayList<Double?>
    return location
}

suspend fun FirebaseUser.getMatchList() : ArrayList<String> {
    val firestore = FirebaseFirestore.getInstance()
    val data = firestore.collection("users").document(this@getMatchList.uid).get().asDeferred().await()
    val match = data.data?.getOrElse("match" , {return arrayListOf()}) as ArrayList<String>
    return match
}

val newUserListener = MutableLiveData<User>()
var currentPage = MutableLiveData<MainPages>()