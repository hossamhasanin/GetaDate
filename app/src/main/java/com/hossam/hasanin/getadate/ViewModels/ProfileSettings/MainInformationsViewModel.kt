package com.hossam.hasanin.getadate.ViewModels.ProfileSettings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hossam.hasanin.getadate.Externals.Constants
import com.hossam.hasanin.getadate.Externals.ErrorTypes
import com.hossam.hasanin.getadate.Models.User
import java.util.*
import kotlin.collections.ArrayList

class MainInformationsViewModel : ViewModel() {

    val storageReference = FirebaseStorage.getInstance().reference
    val mAuth = FirebaseAuth.getInstance()
    val fireStore = FirebaseFirestore.getInstance()
    val randomName = UUID.randomUUID().toString()
    val imagePath: StorageReference =
        storageReference.child("users_thumbs").child("$randomName.jpg")

    val dataSaved: MutableLiveData<Boolean> = MutableLiveData()
    val errorList = ArrayList<ErrorTypes>()

    val ERROR_MESSAGES: HashMap<ErrorTypes, String> = Constants.getErrorMessages()

    fun saveUserData(user : User) {
        user.id = mAuth.currentUser?.uid
        user.email = mAuth.currentUser?.email
        user.username = mAuth.currentUser?.displayName
        user.online = true

        if (checkIfFildesAreCorrect(user)){
            fireStore.collection("users").document(mAuth.currentUser!!.uid).set(user).addOnCompleteListener {
                if (it.isSuccessful){
                    dataSaved.postValue(true)
                } else {
                    throw it.exception!!
                }
            }
        } else {
            dataSaved.postValue(false)
        }
    }

    fun checkIfFildesAreCorrect(user: User) : Boolean{
        errorList.clear()
        if (user.firstName?.trim().equals("")){
            errorList.add(ErrorTypes.FIRSTNAME_ERROR)
        }
        if (user.secondName?.trim().equals("")){
            errorList.add(ErrorTypes.SECONDNAME_ERROR)
        }
        if (user.age!! == null && user.age!! < 10 && user.age!! > 100){
            errorList.add(ErrorTypes.AGE_ERROR)
        }
        if (user.location!![0] == null && user.location!![0] == null){
            errorList.add(ErrorTypes.LOCATION_MISSING)
        }

        if (errorList.isNotEmpty()){
            return false
        }

        return true
    }
}
