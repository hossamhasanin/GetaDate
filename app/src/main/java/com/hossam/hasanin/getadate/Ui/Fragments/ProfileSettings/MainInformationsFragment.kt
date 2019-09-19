package com.hossam.hasanin.getadate.Ui.Fragments.ProfileSettings

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hossam.hasanin.getadate.Externals.Constants
import com.hossam.hasanin.getadate.Externals.LocationHandeler
import com.hossam.hasanin.getadate.Externals.LocationHandeler.locationListener
import com.hossam.hasanin.getadate.Externals.showTheErrors
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings.MainInformationsFactory
import com.hossam.hasanin.getadate.ViewModels.ProfileSettings.MainInformationsViewModel
import kotlinx.android.synthetic.main.main_informations_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MainInformationsFragment : BaseFragment() , KodeinAware {

    override val kodein by closestKodein()
    val mainInformationsFactory : MainInformationsFactory by instance()

    var gender : Int = 1

    private lateinit var viewModel: MainInformationsViewModel

    lateinit var locationManager:LocationManager
    val looper:Looper? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_informations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , mainInformationsFactory).get(MainInformationsViewModel::class.java)

        profile_save.setOnClickListener {
            progressBar_profile.visibility = View.VISIBLE
            profile_save.isClickable = false
            saveUserDataInFireStore()
        }

        viewModel.dataSaved.observe(this , Observer { saved ->
            if (saved){
                Toast.makeText(activity , "تم الحفظ بنجاح" , Toast.LENGTH_LONG).show()
                val action = MainInformationsFragmentDirections.goToCharacteristics(true)
                findNavController().navigate(action)
            } else {
                profile_save.isClickable = true
                showTheErrors(activity!!.applicationContext , viewModel.errorList , viewModel.ERROR_MESSAGES)
            }
        })

        male.setOnClickListener {
            chooseGender(it)
        }

        female.setOnClickListener {
            chooseGender(it)
        }

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        profile_getlocation.setOnClickListener {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this.context!!, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this.activity!!,
                        arrayOf<String?>(permission.ACCESS_FINE_LOCATION),
                        Constants.LOCATION_REQUEST_CODE)
                } else {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER , locationListener , looper)
                    //Log.d("koko", "here")
                    Toast.makeText(activity!!.applicationContext , "تم تحديد الموقع" , Toast.LENGTH_LONG).show()
                }
            } else {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER , locationListener , looper)
                Toast.makeText(activity!!.applicationContext , "تم تحديد الموقع" , Toast.LENGTH_LONG).show()
            }

        }

    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER ,
                    locationListener, looper)
                Toast.makeText(activity!!.applicationContext , "تم تحديد الموقع" , Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity!!.applicationContext , "يجب تحديد الموقع لاكمال عملية التسجيل" , Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveUserDataInFireStore() = launch(Dispatchers.IO) {
        val firstName = profile_first_name.text.toString()
        val secondName = profile_second_name.text.toString()
        val address = profile_address.text.toString()
        val age = if (profile_age.text.toString().isNotEmpty()) profile_age.text.toString().toInt() else -1
        val location = arrayListOf(LocationHandeler.mlocation?.longitude , LocationHandeler.mlocation?.latitude)
        val user = User(firstName = firstName , secondName = secondName , age = age , gender = gender , location = location , address = address)

        viewModel.saveUserData(user)
    }

    fun chooseGender(view: View?){
        if (view?.id == R.id.male){
            gender = 1
            Glide.with(this).load(ContextCompat.getDrawable(activity!!.applicationContext , R.drawable.male_user_icon)).into(profile_image)
        } else {
            gender = 0
            Glide.with(this).load(ContextCompat.getDrawable(activity!!.applicationContext , R.drawable.female_user_icon)).into(profile_image)
        }
    }

}
