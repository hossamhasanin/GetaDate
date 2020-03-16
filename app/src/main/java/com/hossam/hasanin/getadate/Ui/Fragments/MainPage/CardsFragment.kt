package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.messaging.FirebaseMessaging
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.CardsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.CardsViewModel
import kotlinx.android.synthetic.main.cards_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.lang.IllegalStateException

class CardsFragment : BaseMainPageFragment() , KodeinAware{

    override val kodein by closestKodein()

    private val cardsFactory: CardsFactory by instance()

    private lateinit var viewModel: CardsViewModel
    private lateinit var userCharacteristics : MutableList<UserCharacteristic>
    private var index = 0
    var users:MutableList<User>? = null
    private var charQuesNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cards_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.toolbar!!.visibility = View.VISIBLE

        viewModel = ViewModelProviders.of(this , cardsFactory).get(CardsViewModel::class.java)

        launch(IO) {
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

        try{
            getTheData(null)
        } catch (e:Exception){}

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

        more.setOnClickListener {
            val b = userCharacteristics.size - viewModel.chars
            var l: MutableList<UserCharacteristic>? = null
            if (b >= 4){
                more.text = "الصفات التالية"
                l = userCharacteristics.subList(viewModel.chars-1 , 4)
                viewModel.chars = viewModel.chars + 4
            } else if (b < 4 && b > 0){
                more.text = "الصفات التالية"
                l = userCharacteristics.subList(viewModel.chars-1 , userCharacteristics.size)
                viewModel.chars = viewModel.chars + userCharacteristics.size

            } else if (b <= 0){
                more.text = "الصفات الاولى"
                l = userCharacteristics.subList(0 , 4)
                viewModel.chars = 4
            }
            setCharecteristecData(charQuesNum , l!!)
        }


    }

    private fun getTheData(lastUsername : String?) = launch {
        pro.visibility = View.VISIBLE
        users = viewModel.getUsersDidntSeen(lastUsername)
        charQuesNum = viewModel.getCharQuesNum()!!
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
        try {
            text_cont1.text = users?.get(index)?.username
            text_cont2.text = getString(R.string.parameter_age , users?.get(index)?.age.toString())
            //alert_mess.text = getString(R.string.loading_the_characteristics)

            userCharacteristics = viewModel.getUserCharacteristics(users?.get(index)?.id!!).await()
                .toObjects(UserCharacteristic::class.java)

            if (!userCharacteristics.isNullOrEmpty()) {

                Log.v("koko", userCharacteristics.toString())

                val to = if (userCharacteristics.size < 4) userCharacteristics.size else  4
                viewModel.chars = to
                setCharecteristecData(charQuesNum , userCharacteristics.subList(0 , to))

            } else {
                showAlertMess("لا يوجد مرشحين")
            }


            pro.visibility = View.GONE
        } catch (e: Exception){}
    }

    private fun setLikedUser(id : String?) = launch (Dispatchers.IO){
        viewModel.setLikedUser(id).await()
    }

    private fun setDisLikedUser(id: String?) = launch (Dispatchers.IO){
        viewModel.setDisLikedUser(id).await()
    }

    private fun showAlertMess(mess: String){
        try {
            //alert_mess.visibility = View.VISIBLE
            //user_characteristics.visibility = View.GONE
            text_cont1.visibility = View.VISIBLE
            text_cont2.visibility = View.GONE
            pro.visibility = View.GONE
            text_cont1.text = mess
        } catch (e:IllegalStateException){}

    }

    private fun setCharecteristecData(charQuesNum: Int , chars: MutableList<UserCharacteristic>){
        val views = hashMapOf(
            0 to arrayListOf<View>(characteristic_1 , c1_heart_1 , c1_heart_2 , c1_heart_3 , c1_heart_4 , c1_heart_5 , first_card),
            1 to arrayListOf<View>(characteristic_2 , c2_heart_1 , c2_heart_2 , c2_heart_3 , c2_heart_4 , c2_heart_5 , second_card),
            2 to arrayListOf<View>(characteristic_3 , c3_heart_1 , c3_heart_2 , c3_heart_3 , c3_heart_4 , c3_heart_5 , third_card),
            3 to arrayListOf<View>(characteristic_4 , c4_heart_1 , c4_heart_2 , c4_heart_3 , c4_heart_4 , c4_heart_5 , forth_card)
        )

        Log.v("koko" , chars.toString())
        var ratio:Double = 0.0
        for (char in 0..3){
            if (char >= chars.size){
                views[char]?.get(6)?.visibility = View.INVISIBLE
            } else {
                views[char]?.get(6)?.visibility = View.VISIBLE
                (views[char]?.get(0) as TextView).text = chars[char].title
                ratio = chars[char].degree!!.toDouble() / charQuesNum.toDouble()
                setHeartViews(ratio , (views[char]?.get(1) as ImageView) ,
                    (views[char]?.get(2) as ImageView) ,
                    (views[char]?.get(3) as ImageView) ,
                    (views[char]?.get(4) as ImageView) ,
                    (views[char]?.get(5) as ImageView))
            }
        }

    }

    private fun setHeartViews(ratio: Double , heart_1: ImageView , heart_2: ImageView , heart_3: ImageView , heart_4: ImageView , heart_5: ImageView){
        if (ratio != 0.0 && ratio <= 0.2){
            heart_1.setImageResource(R.drawable.ic_favorite_pink)
            heart_2.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.2 && ratio <= 0.5){
            heart_1.setImageResource(R.drawable.ic_favorite_pink)
            heart_2.setImageResource(R.drawable.ic_favorite_pink)
            heart_3.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.5 && ratio <= 0.8){
            heart_1.setImageResource(R.drawable.ic_favorite_pink)
            heart_2.setImageResource(R.drawable.ic_favorite_pink)
            heart_3.setImageResource(R.drawable.ic_favorite_pink)
            heart_4.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.8 && ratio <= 1.0){
            heart_1.setImageResource(R.drawable.ic_favorite_pink)
            heart_2.setImageResource(R.drawable.ic_favorite_pink)
            heart_3.setImageResource(R.drawable.ic_favorite_pink)
            heart_4.setImageResource(R.drawable.ic_favorite_pink)
            heart_5.setImageResource(R.drawable.ic_favorite_pink)
        }
    }


}
