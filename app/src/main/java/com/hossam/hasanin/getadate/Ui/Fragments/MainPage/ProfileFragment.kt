package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestoreException
import com.hossam.hasanin.getadate.Externals.*
import com.hossam.hasanin.getadate.Models.User

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.LoginActivity
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.ProfileFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.ProfileViewModel
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment() , KodeinAware {

    override val kodein by closestKodein()

    private val profileFactory:ProfileFactory by instance()

    private lateinit var viewModel: ProfileViewModel

    lateinit var locationManager:LocationManager
    val looper: Looper? = null

    lateinit var locationMetric:ArrayList<Double?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        activity!!.title_toolbar.text = getString(R.string.profile)
        (activity as MainActivity).currentPage = MainPages.PROFILE


        viewModel = ViewModelProviders.of(this , profileFactory).get(ProfileViewModel::class.java)

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        bindUi()

        logout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(activity , LoginActivity::class.java))
        }

        profile_getlocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this.activity!!,
                        arrayOf<String?>(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.LOCATION_REQUEST_CODE)
                } else {
                    locationManager.requestSingleUpdate(
                        LocationManager.NETWORK_PROVIDER ,
                        LocationHandeler.locationListener, looper)
                    Toast.makeText(activity!!.applicationContext , "انتظر حتى يتم تحديد الموقع" , Toast.LENGTH_LONG).show()
                }
            } else {
                locationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER ,
                    LocationHandeler.locationListener, looper)
                Toast.makeText(activity!!.applicationContext , "انتظر حتى يتم تحديد الموقع" , Toast.LENGTH_LONG).show()
            }
        }

        edit_characteristics.setOnClickListener {
            val action = ProfileFragmentDirections.editCharacteristics(false)
            Navigation.findNavController(it).navigate(action)
        }

        profile_save.setOnClickListener {
            progressBar_profile.visibility = View.VISIBLE
            profile_save.isClickable = false
            upSertUserData()
        }

        LocationHandeler.mlocation.observe(this , Observer {location ->
            if (location != null){
                Toast.makeText(activity!!.applicationContext , "تم تحديد الموقع" , Toast.LENGTH_LONG).show()
            }
        })

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER ,
                    LocationHandeler.locationListener, looper)
            } else {
                Toast.makeText(activity!!.applicationContext , "يجب تحديد الموقع لاكمال عملية التسجيل" , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindUi() = launch {
        LocationHandeler.mlocation.value = null

        val gender:String
        val genderImage: Int
        if (viewModel.currentUser!!.getGender() == 1){
            gender = getString(R.string.gender_male)
            genderImage = R.drawable.male_user_icon
        } else {
            gender = getString(R.string.gender_female)
            genderImage = R.drawable.female_user_icon
        }

        profile_gender.text = gender
        profile_image.setImageResource(genderImage)
        val username = viewModel.currentUser!!.displayName
        profile_username.setText(username)
        val email = viewModel.currentUser!!.email
        profile_email.setText(email)
        val firstName = viewModel.currentUser!!.getFirstName()
        profile_first_name.setText(firstName)
        val secondName = viewModel.currentUser!!.getSecondName()
        profile_second_name.setText(secondName)
        val age = viewModel.currentUser!!.getAge()
        profile_age.setText(age.toString())
        val getAddress = viewModel.currentUser!!.getAddress()
        address.setText(getAddress)

        locationMetric = viewModel.currentUser!!.getLocation()

    }

    private fun upSertUserData() = launch {
        val username = if (profile_username.text.toString().trim().equals("")) viewModel.currentUser!!.displayName else profile_username.text.toString()
        val email = if (profile_email.text.toString().trim().equals("")) viewModel.currentUser!!.email else profile_email.text.toString()
        val firstName = if (profile_first_name.text.toString().trim().equals("")) viewModel.currentUser!!.getFirstName() else profile_first_name.text.toString()
        val secondName = if (profile_second_name.text.toString().trim().equals("")) viewModel.currentUser!!.getSecondName() else profile_second_name.text.toString()
        val address = if (address.text.toString().trim().equals("")) viewModel.currentUser!!.getAddress() else address.text.toString()
        val age = if (!profile_age.text.toString().trim().equals("") || profile_age.text.toString().toInt() in 101 downTo 9) profile_age.text.toString().toInt() else viewModel.currentUser!!.getAge()
        val gender = if (profile_gender.text == getString(R.string.gender_male)) 1 else 0
        if (LocationHandeler.mlocation.value != null){
            locationMetric = arrayListOf(LocationHandeler.mlocation.value!!.longitude , LocationHandeler.mlocation.value!!.latitude)
        }

        val user = User(
            id = viewModel.currentUser!!.uid,
            username = username,
            email = email,
            firstName = firstName,
            secondName = secondName,
            age = age,
            gender = gender,
            location = locationMetric,
            address = address)
        viewModel.upSertUserData(user , this@ProfileFragment.activity!!){

            if (it != null){
                if (it.isSuccessful){
                    Toast.makeText(activity!!.applicationContext , getString(R.string.save_done) , Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity!!.applicationContext , "حدث خطأ ما" , Toast.LENGTH_LONG).show()
                    Log.v("ProfileFragment" , it.exception.toString())
                }
            } else {
                Toast.makeText(activity!!.applicationContext , "تأكد من ان بينات تسجيل الدخول صحيحة" , Toast.LENGTH_LONG).show()
            }
            progressBar_profile.visibility = View.GONE
            profile_save.isClickable = true
        }
    }


}
