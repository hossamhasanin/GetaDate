package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.ViewModel;
import com.etiennelawlor.tinderstack.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.getMatchList

class MatchesViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    val firestore = FirebaseFirestore.getInstance()

}
