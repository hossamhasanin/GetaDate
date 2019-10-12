package com.hossam.hasanin.getadate.ViewModels.ProfileSettings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion

class CharacteristicsViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val fireStore = FirebaseFirestore.getInstance()
    val finshedSaving = MutableLiveData<Boolean>()
    var errorMess:String? = null
    //val characteristics = MutableLiveData<HashMap<String , UserCharacteristic?>>()

    suspend fun saveIntoFireStore(data: HashMap<String , UserCharacteristic?> , questions: HashMap<String , UserCharacteristicQuestion>) {
        if (!data.isNullOrEmpty()){
//            if (deletedData.isNotEmpty()){
//                // delete
//                deletedData.forEach{
//                    fireStore.collection("users").document(mAuth.currentUser!!.uid)
//                        .collection("characteristics").document(it.key).delete()
//                }
//            }
            // add and update
            for ((id , charc) in data){
                fireStore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection("characteristics").document(id).set(charc!!.toHashmap()).asDeferred().await()

                fireStore.collection("users").document(mAuth.currentUser!!.uid)
                    .collection("questions")
                    .document(questions[id]!!.getId()).set(questions[id]!!.toHashmap()).asDeferred().await()
            }

            val numberAnswerdQuestion:Double = questions.size.toDouble()

            val numberQuestions = fireStore.collection("questions")
                .get().asDeferred().await().documents.size

            val personalityRate: Double = numberAnswerdQuestion.div(numberQuestions)

            Log.v("koko" , numberAnswerdQuestion.toString())

            Log.v("koko" , personalityRate.toString())

            fireStore.collection("users").document(mAuth.currentUser!!.uid)
                .update("personalityRate" , personalityRate)


            finshedSaving.postValue(true)
        } else {
            finshedSaving.postValue(false)
            errorMess = "لا تترك الحقول فارغة"
        }
    }

//    fun getCharacteristics(){
//        fireStore.collection("users").document(mAuth.currentUser!!.uid)
//            .collection("characteristics").get().addOnSuccessListener {
//                if (!it.documents.isNullOrEmpty()){
//                    val userCharacteristics = HashMap<String , UserCharacteristic?>()
//                    for (document in it.documents){
//                        val characteristic = document.toObject(UserCharacteristic::class.java)
//                        userCharacteristics[document.id] = characteristic
//                    }
//                    characteristics.postValue(userCharacteristics)
//                }
//            }
//    }

}
