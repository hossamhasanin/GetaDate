package com.hossam.hasanin.getadate.ViewModels.Factories.MainPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hossam.hasanin.getadate.ViewModels.MainPage.AnswerQuestionsViewModel

class AnswerQuestionsFactory : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnswerQuestionsViewModel() as T
    }
}