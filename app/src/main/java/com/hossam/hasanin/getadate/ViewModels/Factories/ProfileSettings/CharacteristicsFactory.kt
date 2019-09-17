package com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.ProfileSettings.CharacteristicsViewModel

class CharacteristicsFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CharacteristicsViewModel() as T
    }

}