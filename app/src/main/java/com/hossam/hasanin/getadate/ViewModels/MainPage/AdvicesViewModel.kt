package com.hossam.hasanin.getadate.ViewModels.MainPage

import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore

class AdvicesViewModel : ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
}
