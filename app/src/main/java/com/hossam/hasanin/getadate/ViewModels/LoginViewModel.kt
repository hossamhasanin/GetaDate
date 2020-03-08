package com.hossam.hasanin.getadate.ViewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.Constants
import com.hossam.hasanin.getadate.Externals.ErrorTypes
import com.hossam.hasanin.getadate.Externals.asDeferred

class LoginViewModel : ViewModel() {
    private val mAuth = FirebaseAuth.getInstance()
    private val errorsList: ArrayList<ErrorTypes> = ArrayList()
    private val firestore = FirebaseFirestore.getInstance()
    val ERROR_MESSAGES:HashMap<ErrorTypes , String> = Constants.getErrorMessages()

    fun login(email: String , password: String , action: (FirebaseUser? , ArrayList<ErrorTypes>) -> Unit){
        errorsList.clear()
        if (checkUserLoginEntries(email , password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    val mUser: FirebaseUser? = it.result?.user
                    mUser!!.getIdToken(true).addOnCompleteListener {
                        val tokenId = it.result?.token
                        firestore.collection("users").document(mAuth.currentUser!!.uid)
                            .update("token_id" , tokenId)
                    }
                    action(mUser , ArrayList())
                } else {
                    errorsList.add(ErrorTypes.USING_SAME_EMAIL)
                    action(null , errorsList)
                }
            }
        } else {
            action(null , errorsList)
        }
    }

    private fun checkUserLoginEntries(email: String , password: String) : Boolean{

        if (email.trim().isEmpty() || !email.trim().contains("@")){
            errorsList.add(ErrorTypes.EMAIL_ERROR)
        }

        if (password.trim().isEmpty() || password.trim().length < 5){
            errorsList.add(ErrorTypes.PASSWORD_ERROR)
        }

        if (errorsList.isNotEmpty()){
            return false
        }

        return true
    }

    fun signup(email: String , username: String , password: String , action : (FirebaseUser? , ArrayList<ErrorTypes>) -> Unit){
        errorsList.clear()
        if (checkUserSignupEntries(email , username, password)){
            mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener {
                if (it.isSuccessful){
                    val mUser: FirebaseUser? = it.result?.user

                    mUser?.getIdToken(true)?.addOnCompleteListener {
                        val tokenId = it.result?.token
                        firestore.collection("users").document(mAuth.currentUser!!.uid)
                            .update("token_id" , tokenId)
                    }

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

}