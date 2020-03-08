package com.hossam.hasanin.getadate.ViewModels.SignupLogin

import android.net.ParseException
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.Constants
import com.hossam.hasanin.getadate.Externals.ErrorTypes
import com.hossam.hasanin.getadate.Models.User
import org.joda.time.LocalDate
import org.joda.time.Years
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SignupViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val errorsList: ArrayList<ErrorTypes> = ArrayList()
    private val firestore = FirebaseFirestore.getInstance()
    val ERROR_MESSAGES:HashMap<ErrorTypes , String> = Constants.getErrorMessages()


    fun signup(email: String , username: String , address: String , password: String , gender: Int ,date: String , govern: String , action : (FirebaseUser?, ArrayList<ErrorTypes>) -> Unit){
        errorsList.clear()
        if (checkUserSignupEntries(email , username, password)){
            mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener {
                if (it.isSuccessful){
                    val mUser: FirebaseUser? = it.result?.user

//                    mUser?.getIdToken(true)?.addOnCompleteListener {
//                        val tokenId = it.result?.token
//                        firestore.collection("users").document(mAuth.currentUser!!.uid)
//                            .update("token_id" , tokenId)
//                    }
                    val birth = date.split("/")
                    val age = getAge(birth[2].toInt() , birth[1].toInt() , birth[0].toInt())
                    val user = User(username = username , email = email , address = address , gender = gender , governorate = govern , age = age)
                    val userRef = firestore.collection("users").document(mAuth.currentUser!!.uid)
                    userRef.set(user.toHashmap())

                    userRef.collection("characteristics").document()


                    mUser?.sendEmailVerification()?.addOnSuccessListener {
                        val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(username).build()
                        mUser.updateProfile(profileChangeRequest).addOnCompleteListener {
                            if (it.isSuccessful){
                                action(mUser , errorsList)
                            }
                        }
                    }
                } else {
                    errorsList.add(ErrorTypes.FIREBASE_SERVER_ERROR)
                    action(null , errorsList)
                }
            }
        } else {
            action(null , errorsList)
        }
    }

    private fun checkUserSignupEntries(email: String , username: String , password: String) : Boolean{
        if (email.trim().isEmpty() || !email.trim().contains("@")){
            errorsList.add(ErrorTypes.EMAIL_ERROR)
        }

        if (password.trim().isEmpty() || password.trim().length < 5){
            errorsList.add(ErrorTypes.PASSWORD_ERROR)
        }

        if (username.trim().isEmpty()){
            errorsList.add(ErrorTypes.USERNAME_ERROR)
        }

        if (errorsList.isNotEmpty()){
            return false
        }

        return true
    }

    fun getAge(year: Int , month: Int , day : Int): Int {
        val birthData = LocalDate(year , month , day)
        val now = LocalDate()
        val age = Years.yearsBetween(birthData , now)

        return age.years
    }

}
