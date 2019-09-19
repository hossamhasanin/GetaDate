package com.hossam.hasanin.getadate.ViewModels.ProfileSettings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Models.UserCharacteristic

class CharacteristicsViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val fireStore = FirebaseFirestore.getInstance()
    val finshedSaving = MutableLiveData<Boolean>()
    var errorMess:String? = null
    val characteristics = MutableLiveData<HashMap<String , UserCharacteristic?>>()

    suspend fun saveIntoFireStore(data: HashMap<String , UserCharacteristic?> , deletedData : HashMap<String , UserCharacteristic?>) {
        if (!data.isNullOrEmpty()){
            if (deletedData.isNotEmpty()){
                // delete
                deletedData.forEach{
                    fireStore.collection("users").document(mAuth.currentUser!!.uid)
                        .collection("characteristics").document(it.key).delete()
                }
            }
            // add and update
            for ((id , charc) in data){
                fireStore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection("characteristics").document(id).set(charc!!.toHashmap()).asDeferred().await()
            }
            finshedSaving.postValue(true)
        } else {
            finshedSaving.postValue(false)
            errorMess = "لا تترك الحقول فارغة"
        }
    }

    fun getCharacteristics(){
        fireStore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("characteristics").get().addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()){
                    val userCharacteristics = HashMap<String , UserCharacteristic?>()
                    for (document in it.documents){
                        val characteristic = document.toObject(UserCharacteristic::class.java)
                        userCharacteristics[document.id] = characteristic
                    }
                    characteristics.postValue(userCharacteristics)
                }
            }
    }

}
