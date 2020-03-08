package com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.SignupLogin.LoginViewModel

class LoginFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel() as T
    }
}