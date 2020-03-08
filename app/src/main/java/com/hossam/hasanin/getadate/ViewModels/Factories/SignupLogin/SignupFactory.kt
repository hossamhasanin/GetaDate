package com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.SignupLogin.SignupViewModel

class SignupFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignupViewModel() as T
    }
}