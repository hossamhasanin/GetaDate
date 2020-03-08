package com.hossam.hasanin.getadate

import android.app.Application
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Ui.Adapter.CharacteristicsAdapter
import com.hossam.hasanin.getadate.ViewModels.Factories.LauncherFactory
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.*
import com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings.CharacteristicsFactory
import com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings.MainInformationsFactory
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.LoginFactory
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.SignupChoosingGenderFactory
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.SignupFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.factory
import org.kodein.di.generic.provider

class Application : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        bind() from provider { LauncherFactory() }
        bind() from provider { MainInformationsFactory() }
        bind() from provider { CharacteristicsFactory() }
        bind() from  factory { options : FirestoreRecyclerOptions<Characteristic?> -> CharacteristicsAdapter(options) }
        bind() from provider { CardsFactory() }
        bind() from provider { ProfileFactory(this@Application) }
        bind() from provider { MatchesFactory() }
        bind() from provider { ShowUserFactory() }
        bind() from provider { ReserveResturantFactory() }
        bind() from provider { PickTimeFactory() }
        bind() from provider { DatingDetailsFactory() }
        bind() from provider { AdvicesFactory() }
        bind() from provider { EnhancePersonalityFactory() }
        bind() from provider { ModifyQuestionsFactory() }
        bind() from provider { EditCharacteristicsFactory() }
        bind() from provider { LoginFactory() }
        bind() from provider { SignupFactory() }
        bind() from provider { SignupChoosingGenderFactory() }
        bind() from provider { AnswerQuestionsFactory() }

    }

    override fun onCreate() {
        super.onCreate()
        val mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(mAuth.currentUser!!.uid)
        }
    }

}