package com.hossam.hasanin.getadate.ViewModels.MainPage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion
import com.hossam.hasanin.getadate.Ui.Adapter.AnswersItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.lang.Exception

class AnswerQuestionsViewModel : ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    var questions:MutableList<CharacteristicQuestions>? = null
    val viewItems:MutableLiveData<CharacteristicQuestions> = MutableLiveData()
    var currentItem = 0
    val limitRecords:Long = 20
    var gender:Int? = null
    var userQues:List<String?>? = null
    var userAnswers:List<String?>? = null
    val userRef = firestore.collection("users").document(mAuth.currentUser!!.uid)
    var edit:Boolean = false

    suspend fun getQuestions(){
        viewItems.postValue(CharacteristicQuestions().copy(loading = true , error = null))
        gender = mAuth.currentUser!!.getGender()
        firestore.collection("questions").whereEqualTo("gender" , gender).orderBy("cId").limit(limitRecords).get().addOnSuccessListener { allQues ->

            userRef.collection("questions").get().addOnSuccessListener { userQ ->
                    questions = allQues.toObjects(CharacteristicQuestions::class.java)
                    if (!userQ.isEmpty){
                        userQues = userQ.toObjects(UserCharacteristicQuestion::class.java).map { ques ->
                            ques.title
                        }
                        userAnswers = userQ.toObjects(UserCharacteristicQuestion::class.java).map { ques ->
                            ques.answer
                        }
                        if (edit){
                            var j = 0
                            for (q in questions!!){
                                if (j < userAnswers!!.size){
                                    Log.v("koko" , "coun $j")
                                    q.selected = q.answers!!.indexOf(userAnswers!![j])
                                    j++
                                } else {
                                    break
                                }
                            }
                        } else {
                            questions!!.retainAll { finalQues ->
                                return@retainAll !userQues!!.contains(finalQues.title)
                            }
                        }
                    }
                    Log.v("koko" , "ques ${questions!!}")
                    viewItems.postValue(questions!![currentItem].copy(loading = false , error = null))
                }
        }
    }

    fun goToNextQuestion(){
        currentItem++
        if (currentItem < questions!!.size){
            viewItems.postValue(questions!![currentItem].copy(loading = false , error = null))
            Log.v("koko" , "incr $currentItem")
        } else {
            viewItems.postValue(CharacteristicQuestions().copy(loading = true , error = null))
            firestore.collection("questions").whereEqualTo("gender" , gender).orderBy("cId")
                .startAfter(questions!!.last().title).limit(limitRecords).get().addOnSuccessListener { allQues->
                    if (!allQues.isEmpty){
                        questions = allQues.toObjects(CharacteristicQuestions::class.java)
                        if (userQues != null){
                            if (edit){
                                var j = 0
                                for (q in questions!!){
                                    if (j < userAnswers!!.size){
                                        Log.v("koko" , "coun $j")
                                        q.selected = q.answers!!.indexOf(userAnswers!![j])
                                        j++
                                    } else {
                                        break
                                    }
                                }
                            } else {
                                questions!!.retainAll { finalQues ->
                                    return@retainAll !userQues!!.contains(finalQues.title)
                                }
                            }
                        }
                        currentItem = 0
                        viewItems.postValue(questions!![currentItem].copy(loading = false , error = null))
                    } else {
                        viewItems.postValue(CharacteristicQuestions().copy(loading = false , error = Exception("معتش في اسئلة عظمة اوي كدا")))
                    }
                }
        }
    }

    fun submitTheAnswer(answerItem: AnswersItem){
        val submittedQuestion = UserCharacteristicQuestion()
        submittedQuestion.answer = answerItem.answer
        submittedQuestion.title = questions!![currentItem].title
        submittedQuestion.cId = questions!![currentItem].cId
        userRef.collection("questions").add(submittedQuestion.toHashmap())

        userRef.collection("characteristics")
            .document(submittedQuestion.cId!!).get().addOnSuccessListener {
                if (it.exists()){
                    val degree = it.toObject(UserCharacteristic::class.java)!!.degree?.plus(1)
                    userRef.collection("characteristics").document(submittedQuestion.cId!!).update("degree" , degree)
                } else {
                    firestore.collection("characteristics").document(submittedQuestion.cId!!).get().addOnSuccessListener {snapCaharc ->
                        val charc = snapCaharc.toObject(Characteristic::class.java)
                        val userCharc = UserCharacteristic()
                        userCharc.title = charc!!.title
                        userCharc.order = charc.order
                        userCharc.degree = 1

                        userRef.collection("characteristics").document(submittedQuestion.cId!!)
                            .set(userCharc.toHashmap())
                    }
                }

            }
    }

}
