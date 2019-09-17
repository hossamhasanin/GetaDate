package com.hossam.hasanin.getadate.ViewModels.MainPage

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.MainActivity
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.re_sign_in.view.*

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()

    fun logout(){
        mAuth.signOut()
    }

    fun upSertUserData(user:User , activity: Activity , action: (Task<*>?) -> Unit){
        if (!user.email.equals(currentUser!!.email) || !user.username.equals(currentUser.displayName)){
            showReSignInWindow(activity) { login ->
                if (login != null){
                    if (login.isSuccessful){
                        firestore.collection("users").document(currentUser.uid).update(user.toHashmap()).addOnCompleteListener {
                            if (!user.email.equals(currentUser!!.email)) {
                                mAuth.currentUser!!.updateEmail(user.email!!).addOnCompleteListener { task ->
                                    mAuth.currentUser!!.sendEmailVerification()
                                    action(task)
                                }
                            }
                            if (!user.username.equals(currentUser.displayName)){
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.username)
                                    .build()
                                mAuth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                    action(task)
                                }
                            }
                        }
                    } else {
                        action(login)
                    }
                } else {
                    action(login)
                }
            }
        } else {
            firestore.collection("users").document(currentUser.uid).update(user.toHashmap()).addOnCompleteListener {
                action(it)
            }
        }

    }

    private fun showReSignInWindow(activity:Activity , action: (Task<AuthResult>?) -> Unit){
        (activity as MainActivity )



        var ad : AlertDialog? = null
        val popupWindow = AlertDialog.Builder(activity)
        val layout = activity.layoutInflater.inflate(R.layout.re_sign_in , null)
        popupWindow.setView(layout)
        popupWindow.setCancelable(false)
        popupWindow.setPositiveButton(activity.getString(R.string.login_button)) { dialog , id ->

            val email = layout.email_re.text.toString()
            val password = layout.password_re.text.toString()
            ad?.dismiss()
            Log.v("ProfileFragment" , "e $email p $password")
            if (email.trim().isNotEmpty() && password.trim().isNotEmpty()){
                mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener {
                    action(it)
                }
            } else {
                action(null)
            }

        }

        popupWindow.setNegativeButton("أغلق"){d , id ->
            activity.progressBar_profile.visibility = View.GONE
            activity.profile_save.isClickable = true
        }

        ad = popupWindow.create()
        ad.show()
    }
}
