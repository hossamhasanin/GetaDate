package com.hossam.hasanin.getadate.ViewModels.ProfileSettings

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

    suspend fun saveIntoFireStore(data: HashMap<String , UserCharacteristic>) {
        if (!data.isNullOrEmpty()){
            for ((id , characteristic) in data){
                fireStore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection("characteristics").document(id).set(characteristic.toHashmap()).asDeferred().await()
            }
            finshedSaving.postValue(true)
        } else {
            finshedSaving.postValue(false)
            errorMess = "لا تترك الحقول فارغة"
        }
    }

}
