package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion
import com.hossam.hasanin.getadate.Ui.Fragments.MainPage.EditCharacteristicCard

class EditCharacteristicsViewModel : ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val characteristics = MutableLiveData<List<UserCharacteristic>?>()
    val deletingListener = MutableLiveData<EditCharacteristicCard>()
    val errorMess = MutableLiveData<String>()

    fun getUserCharacteristics(){
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("characteristics").get().addOnCompleteListener {
                if (it.isSuccessful){
                    val data = it.result!!.toObjects(UserCharacteristic::class.java)
                    if (data.isNotEmpty()){
                        var i = 0
                        it.result!!.documents.forEach {doc ->
                            data[i].withId(doc.id)
                            i++
                        }
                        characteristics.postValue(data)
                    } else {
                        characteristics.postValue(null)
                    }
                } else {
                    characteristics.postValue(null)
                }
            }
    }

    fun saveChanges(data : UserCharacteristic) : Task<Void>{
        return firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("characteristics").document(data.getId()!!).set(data.toHashmap())
    }

    suspend fun deleteCharacteristic(id : String){

        if (checkIfThereIsOneCharacter()){
            firestore.collection("users").document(mAuth.currentUser!!.uid)
                .collection("characteristics").document(id).delete().addOnSuccessListener {

                    firestore.collection("users").document(mAuth.currentUser!!.uid)
                        .collection("questions").whereEqualTo("cId" , id).get().addOnSuccessListener { questions ->
                            if (questions.documents.isNotEmpty()){
                                questions.documents.forEach {doc ->
                                    firestore.collection("users").document(mAuth.currentUser!!.uid)
                                        .collection("questions").document(doc.id).delete()
                                }
                            }
                        }

                }
        } else {
            errorMess.postValue("لا يمكن مسح جميع الصفات")
        }
    }

    suspend fun checkIfThereIsOneCharacter() : Boolean{
        val task = firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("characteristics").get().asDeferred().await()
        val chars = task.toObjects(UserCharacteristic::class.java)
        return chars.size > 1
    }
}
