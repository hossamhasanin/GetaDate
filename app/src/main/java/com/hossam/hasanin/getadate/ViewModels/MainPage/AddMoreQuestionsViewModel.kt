package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion
import kotlinx.coroutines.*

class AddMoreQuestionsViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val questions = MutableLiveData<List<CharacteristicQuestions>>()

    fun getQuestions(cId : String){
        firestore.collection("questions").whereEqualTo("cId" , cId)
            .get().addOnCompleteListener {
            if (it.isSuccessful){
                val ques = it.result!!.toObjects(CharacteristicQuestions::class.java)
                var i = 0
                it.result!!.documents.forEach {u ->
                    ques[i].withId(u.id)
                    i += 1
                }
                questions.postValue(ques)
            } else {
                questions.postValue(null)
            }
        }
    }

    suspend fun getUserQuestions(cId: String) : List<UserCharacteristicQuestion>{
        return firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("questions").whereEqualTo("cId" , cId)
            .get().asDeferred().await().toObjects(UserCharacteristicQuestion::class.java)
    }

    @ExperimentalCoroutinesApi
    fun saveTheQuestion(question: UserCharacteristicQuestion){
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("questions").document(question.getId()).set(question.toHashmap()).addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    modifyPersonalityRate()

                    this.cancel()
                }
            }
    }

    fun deleteTheQuestion(id: String){
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("questions").document(id).delete().addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    modifyPersonalityRate()

                    this.cancel()
                }
            }
    }

    suspend fun modifyPersonalityRate(){
        val userQuestionsCount = firestore.collection("users").document(mAuth.currentUser!!.uid)
            .collection("questions").get().asDeferred().await().documents.size.toDouble()
        val questionsCount = firestore.collection("questions").get().asDeferred().await().documents.size.toDouble()

        val personalityRate = userQuestionsCount / questionsCount
        firestore.collection("users").document(mAuth.currentUser!!.uid)
            .update("personalityRate" , personalityRate)
    }

}
