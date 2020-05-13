package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.content.Intent
import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.Externals.getLocation
import com.hossam.hasanin.getadate.Models.DatingRequest
import com.hossam.hasanin.getadate.Models.Resturant

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.DatingDetailsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.DatingDetailsViewModel
import kotlinx.android.synthetic.main.dating_details_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class DatingDetailsFragment : BaseMainPageFragment() , KodeinAware {

    private lateinit var viewModel: DatingDetailsViewModel

    override val kodein by closestKodein()

    private val datingDetailsFactory: DatingDetailsFactory by instance()

    var requestId:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dating_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.toolbar!!.visibility = View.GONE

        viewModel = ViewModelProviders.of(this , datingDetailsFactory).get(DatingDetailsViewModel::class.java)

        val args = arguments?.let { DatingDetailsFragmentArgs.fromBundle(it) }
        requestId = args?.requestId

        viewModel.getRequestDetails(requestId!!)

        viewModel.resturantListener.observe(this , Observer {
            bindPlaceData(it)
        })

        viewModel.requestListener.observe(this , Observer {
            bindMainInfo(it)
        })

    }

    private fun bindPlaceData(place: Resturant?) = launch {
        if (place != null){
            Glide.with(this@DatingDetailsFragment.activity!!).load(place.image!!.toUri()).into(resturant_image)
            resturant_name.text = place.name
            address.text = place.address

            val resturantLoc = Location("")
            resturantLoc.longitude = place.location?.get(0)!!
            resturantLoc.latitude = place.location.get(1)!!

            val currentUserLocationArray = viewModel.currentUser?.getLocation()
            val currentUserLocation = Location("")
            currentUserLocation.longitude = currentUserLocationArray!![0]!!
            currentUserLocation.latitude = currentUserLocationArray[1]!!

            val calculateDistance = currentUserLocation.distanceTo(resturantLoc)

            distance.text = getString(R.string.parameter_distance , String.format("%.2f", calculateDistance))

            open_map.setOnClickListener {
                val geoLocation = "geo:0,0?q=${place.location?.get(1)},${place.location?.get(0)}(${place.name})"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = geoLocation.toUri()
                }
                val packManger = activity!!.packageManager
                if (intent.resolveActivity(packManger) != null){
                    startActivity(intent)
                }
            }

        }
    }

    private fun bindMainInfo(request: DatingRequest?){
        if (request != null){
            date.text = getString(R.string.parameter_date , request.date)
            time.text = getString(R.string.parameter_time , request.time)

            setTheState(request)
        }
    }

    private fun setTheState(request: DatingRequest) = launch {
        if (request.placeAccepted!!){

            place_state.text = getString(R.string.all_accepted)

            if (viewModel.currentUser!!.getGender() == 1){
                place_action.text = getString(R.string.change)
                place_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_edit_24dp) , null , null , null)
                place_action.setBackgroundResource(R.drawable.rounded_button_baby_blue)

                place_action.setOnClickListener {
                    val action = DatingDetailsFragmentDirections.goToResetPlace(request.to , request.getId())
                    Navigation.findNavController(it).navigate(action)
                }

            } else {
                place_action.text = getString(R.string.not_suitable)
                place_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_close_24dp) , null , null , null)
                place_action.setBackgroundResource(R.drawable.rounded_red_box)

                place_action.setOnClickListener {
                    viewModel.refuseThePlace(request.getId()!!)
                }

            }

            place_action.visibility = View.VISIBLE
        } else {

            place_state.text = getString(R.string.didnt_accept_yet)

            if (viewModel.currentUser!!.getGender() == 1){
                place_action.text = getString(R.string.change)
                place_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_edit_24dp) , null , null , null)
                place_action.setBackgroundResource(R.drawable.rounded_button_baby_blue)

                place_action.setOnClickListener {
                    val action = DatingDetailsFragmentDirections.goToResetPlace(request.to , request.getId())
                    Navigation.findNavController(it).navigate(action)
                }

                place_action.visibility = View.VISIBLE
            } else {
                place_action.visibility = View.GONE
            }
        }

        place_state.visibility = View.VISIBLE

        if (request.timeAccepted!!){

            time_state.text = getString(R.string.all_accepted)

            if (viewModel.currentUser!!.getGender() == 0){
                time_action.text = getString(R.string.change)
                time_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_edit_24dp) , null , null , null)
                time_action.setBackgroundResource(R.drawable.rounded_button_baby_blue)

                time_action.setOnClickListener {
                    val action = DatingDetailsFragmentDirections.goToResetTime(request.getId() , request.to!!)
                    Navigation.findNavController(it).navigate(action)
                }

            } else {
                time_action.text = getString(R.string.not_suitable)
                time_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_close_24dp) , null , null , null)
                time_action.setBackgroundResource(R.drawable.rounded_red_box)

                time_action.setOnClickListener {
                    viewModel.refuseTheTime(request.getId()!!)
                }

            }

            time_action.visibility = View.VISIBLE

        } else {
            time_state.text = getString(R.string.didnt_accept_yet)

            if (viewModel.currentUser!!.getGender() == 0){
                time_action.text = getString(R.string.change)
                time_action.setCompoundDrawables(ContextCompat.getDrawable(this@DatingDetailsFragment.activity!! , R.drawable.ic_edit_24dp) , null , null , null)
                time_action.setBackgroundResource(R.drawable.rounded_button_baby_blue)

                time_action.setOnClickListener {
                    val action = DatingDetailsFragmentDirections.goToResetTime(request.getId() , request.to!!)
                    Navigation.findNavController(it).navigate(action)
                }

                time_action.visibility = View.VISIBLE

            } else {
                time_action.visibility = View.GONE
            }
        }

        time_state.visibility = View.VISIBLE

    }


}
