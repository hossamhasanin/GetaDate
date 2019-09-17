package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.hossam.hasanin.getadate.Externals.convertToListItems
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.CardsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.CardsViewModel
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.cards_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CardsFragment : BaseFragment() , KodeinAware{

    override val kodein by closestKodein()

    private val cardsFactory: CardsFactory by instance()

    private lateinit var viewModel: CardsViewModel
    private lateinit var userCharacteristics : MutableList<UserCharacteristic>
    private var index = 0
    var users:MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cards_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.left_icon.setImageResource(R.drawable.ic_account_circle_24dp)
        activity!!.right_icon.setImageResource(R.drawable.ic_couple_24dp)
        activity!!.title_toolbar.text = getString(R.string.main_page)
        activity!!.left_icon.visibility = View.VISIBLE
        activity!!.right_icon.visibility = View.VISIBLE
        (activity as MainActivity).currentPage = MainPages.CARDS


        viewModel = ViewModelProviders.of(this , cardsFactory).get(CardsViewModel::class.java)

        launch {
            val topic = if (viewModel.currentUser!!.getGender() == 1){
                "female"
            } else {
                "male"
            }
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }


//        newUserListener.observe(this , Observer {
//            if (index >= users!!.size){
//                users = arrayListOf(it)
//                index = 0
//
//                launch(Dispatchers.Main) {
//                    bindUi()
//                }
//            }
//            Log.v("koko" , "noti obs")
//        })



        val slideIn: Animation =
            AnimationUtils.loadAnimation(activity!!.applicationContext, R.anim.slide_left_to_right)

        val slideOut: Animation =
            AnimationUtils.loadAnimation(activity!!.applicationContext, R.anim.slide_right_to_left)

        getTheData(null)

        like.setOnClickListener {
            if (users != null) {
                launch {
                    if (users!!.size != 0){
                        cardView.startAnimation(slideOut)
                        setLikedUser(users?.get(index)!!.id)
                        index += 1
                        if (index < users!!.size){
                            bindUi()
                        } else {
                            getTheData(users?.get(users!!.size - 1)?.username)
                        }
                    }
                }
            }
        }

        dislike.setOnClickListener {
            if (users != null) {
                launch {
                    if (users!!.size != 0){
                        cardView.startAnimation(slideIn)
                        setDisLikedUser(users?.get(index)!!.id)
                        index += 1
                        if (index < users!!.size){
                            bindUi()
                        } else {
                            getTheData(users?.get(users!!.size - 1)?.username)
                        }
                    }
                }
            }
        }


    }

    private fun getTheData(lastUsername : String?) = launch {
        pro.visibility = View.VISIBLE
        users = viewModel.getUsersDidntSeen(lastUsername)
        if (users!!.isNotEmpty()){
            index = 0

            bindUi()

            //index += 1
        } else {
            if (index > 0){
                // this will show after the list of users gets empty
                showAlertMess(getString(R.string.no_other_candidates))
            } else {
                // this will show if there is no users in the list from the first place
                showAlertMess(getString(R.string.no_users_found))
            }
            //Log.v("koko" , users.size.toString())
        }

    }



    private suspend fun bindUi() {
        user_card_username.text = users?.get(index)?.username
        user_card_validaty.text = "موثق"
        alert_mess.text = getString(R.string.loading_the_characteristics)

        userCharacteristics = viewModel.getUserCharacteristics(users?.get(index)?.id!!).await().toObjects(UserCharacteristic::class.java)

        if (!userCharacteristics.isNullOrEmpty()){

            Log.v("koko" , userCharacteristics.toString())

            alert_mess.visibility = View.GONE
            user_characteristics.visibility = View.VISIBLE

            val characteristicItems = userCharacteristics.convertToListItems()

            val groupAdapter = GroupAdapter<ViewHolder>().apply {
                spanCount = 2
                addAll(if (characteristicItems.size > 1) characteristicItems.take(4) else characteristicItems.take(1))
            }

            user_characteristics.apply {
                layoutManager = GridLayoutManager(this@CardsFragment.activity , groupAdapter.spanCount).apply {
                    spanSizeLookup = groupAdapter.spanSizeLookup
                }
                adapter = groupAdapter
            }

            if (characteristicItems.size > 4){
                ExpandableGroup(ExpandableHeader("أكمل الصفات" , cardView , like , dislike), false).apply {
                    add(Section(characteristicItems.takeLast(characteristicItems.size-4)))
                    groupAdapter.add(this)
                }
            }

        } else {
            showAlertMess(getString(R.string.characteristics_not_found))
        }


        pro.visibility = View.GONE
    }

    private fun setLikedUser(id : String?) = launch (Dispatchers.IO){
        viewModel.setLikedUser(id).await()
    }

    private fun setDisLikedUser(id: String?) = launch (Dispatchers.IO){
        viewModel.setDisLikedUser(id).await()
    }

    private fun showAlertMess(mess: String){
        alert_mess.visibility = View.VISIBLE
        user_characteristics.visibility = View.GONE
        user_card_validaty.visibility = View.GONE
        user_card_username.visibility = View.GONE
        pro.visibility = View.GONE
        alert_mess.text = mess
    }

}
