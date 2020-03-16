package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.app.AlertDialog
import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.hossam.hasanin.getadate.Externals.*
import com.hossam.hasanin.getadate.Models.DatingRequest
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.Models.UserCharacteristic

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.ShowUserFactory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.show_user_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ShowUserFragment : BaseMainPageFragment() , KodeinAware {

    override val kodein by closestKodein()

    private val showUserFactory:ShowUserFactory by instance()

    private lateinit var viewModel: ShowUserViewModel

    private var finishedTasks = 0

    private var charQuesNum = 0

    var id: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.show_user_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.toolbar!!.visibility = View.GONE

        viewModel = ViewModelProviders.of(this , showUserFactory).get(ShowUserViewModel::class.java)

        loading_bar.visibility = View.VISIBLE

        var safeArgs = arguments?.let { ShowUserFragmentArgs.fromBundle(it) }
        id = safeArgs?.id

        try {
            viewModel.getUseBasicInfo(id!!)
        } catch (e:NotFoundUserException){}

        try {
            viewModel.getUserChaacteristics(id!!)
        } catch (e:CharacteristicsNotFound){}

        viewModel.getUserRequest(id!!)

        viewModel.getUser.observe(this , Observer {
            bindBasicUserInfo(it)
            Log.v("koko" , "user")
        })

        viewModel.getUserCharacteristicsList.observe(this , Observer {
            bindRec(it)
            Log.v("koko" , "cha")
        })

        viewModel.getDatingRequest.observe(this , Observer {
            bindDatingRequest(it)
            loading_bar.visibility = View.GONE
            Log.v("koko" , "date")
        })

//        viewModel.tasksFinished.observe(this , Observer {
//            finishedTasks += 1
//            if (finishedTasks >= 3){
//            }
//            Log.v("koko" , "n $finishedTasks")
//        })


    }

    private fun bindBasicUserInfo(user:User) = launch {
        user_age.text = getString(R.string.parameter_age , user.age.toString())
        val userLoc = Location("")
        userLoc.longitude = user.location?.get(0)!!
        userLoc.latitude = user.location?.get(1)!!

        val currentUserLocationArray = viewModel.currentUser?.getLocation()
        val currentUserLocation = Location("")
        currentUserLocation.longitude = currentUserLocationArray!![0]!!
        currentUserLocation.latitude = currentUserLocationArray[1]!!

        val calculateDistance = currentUserLocation.distanceTo(userLoc)
        user_distance.text =  getString(R.string.parameter_distance , calculateDistance.toString())
        username.text = getString(R.string.parameter_username , user.username)
        address.text = getString(R.string.parameter_address , user.address)
       // user_firstname.text = getString(R.string.parameter_firstname , user.firstName)
        //user_secondname.text = getString(R.string.parameter_second_name , user.secondName)
    }

    private fun bindRec(characteristics : MutableList<UserCharacteristic>) = launch (Main){
        charQuesNum = viewModel.firestore.getCharQuesNum()!!
        val items = characteristics.convertToListItems(charQuesNum)

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 2
            addAll(items)
        }

        user_charecteristic.apply {
            layoutManager = GridLayoutManager(this@ShowUserFragment.activity , groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

    }

    private fun bindDatingRequest(datingRequest: DatingRequest?){
        user_request_state.visibility = View.VISIBLE
        if (datingRequest != null){
            if (datingRequest.status == 0){
                showSuspendedRequestButtons(datingRequest.from , datingRequest.getId())
                user_request_state.text = getString(R.string.not_accepted_yet)
            } else {
                showAcceptedRequestButtons(datingRequest.getId()!!)
                user_request_state.text = getString(R.string.accepted_request)
            }
        } else {
            showNoRequestButtons(null)
            user_request_state.text = getString(R.string.no_dating_request)
        }

    }

    private fun showSuspendedRequestButtons(sender: String? , requestId: String?){
        if (sender == viewModel.currentUser!!.uid){
            right_btn.setBackgroundResource(R.drawable.rounded_red_box)
            right_btn.text = getString(R.string.deleting_request)

            right_btn.setOnClickListener {
                viewModel.deleteTheRequest(requestId!!)
            }

            left_btn.visibility = View.GONE

        } else {
            right_btn.setBackgroundResource(R.drawable.rounded_green_box)
            right_btn.text = getString(R.string.accepting)

            right_btn.setOnClickListener {
                goToMakeRequest(it , requestId)
            }

            left_btn.visibility = View.GONE
        }

        right_btn.visibility = View.VISIBLE
        advices.visibility = View.GONE
    }

    private fun showAcceptedRequestButtons(requestId: String){
        right_btn.setBackgroundResource(R.drawable.rounded_red_box)
        right_btn.text = getString(R.string.deleting_request)

        right_btn.setOnClickListener {
            viewModel.deleteTheRequest(requestId)
        }

        left_btn.setBackgroundResource(R.drawable.rounded_button_baby_blue)
        left_btn.text = getString(R.string.dating_details)

        left_btn.setOnClickListener {
            val action = ShowUserFragmentDirections.goToDatingDetails(requestId)
            Navigation.findNavController(it).navigate(action)
        }

        advices.text = getString(R.string.advices_for_the_date)

        advices.setOnClickListener {
            val action = ShowUserFragmentDirections.goToAdvices()
            Navigation.findNavController(it).navigate(action)
        }

        right_btn.visibility = View.VISIBLE
        left_btn.visibility = View.VISIBLE
        advices.visibility = View.VISIBLE
    }

    private fun showNoRequestButtons(requestId: String?){
        right_btn.setBackgroundResource(R.drawable.rounded_green_box)
        right_btn.text = getString(R.string.wanna_meet)

        right_btn.setOnClickListener {
            goToMakeRequest(it , requestId)
        }

        right_btn.visibility = View.VISIBLE
        left_btn.visibility = View.GONE
        advices.visibility = View.GONE
    }

    private fun goToMakeRequest(view: View , requestId: String?) = launch{
        val action = if (viewModel.currentUser!!.getGender() == 1){
            ShowUserFragmentDirections.goToReserveResturant(id , requestId)
            //ShowUserFragmentDirections.goToSetTheTime(requestId , id!!)
        } else {
            ShowUserFragmentDirections.goToSetTheTime(requestId , id!!)
        }
        Navigation.findNavController(view).navigate(action)
    }

//    if (datingRequest != null){
//        if (datingRequest.from!! == viewModel.currentUser!!.uid){
//            // the current user is the sender
//            if (datingRequest.status == 0){
//                // the request didn't accepted from the the other user yet
//
//
//
//            } else {
//                // the request accepted
//            }
//        } else if(datingRequest.to!! == viewModel.currentUser!!.uid) {
//            // the current user is the receiver
//            if (datingRequest.status == 0){
//                // the request didn't accepted yet
//            } else {
//                // the request has accepted
//            }
//        }
//    } else {
//        // no request yet
//    }

}
