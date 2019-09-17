package com.hossam.hasanin.getadate.ViewModels.Factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.LauncherViewModel

class LauncherFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LauncherViewModel() as T
    }
}