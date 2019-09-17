package com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.ProfileSettings.MainInformationsViewModel

class MainInformationsFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainInformationsViewModel() as T
    }
}