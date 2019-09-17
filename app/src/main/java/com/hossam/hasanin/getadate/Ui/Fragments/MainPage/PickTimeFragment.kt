package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.PickTimeFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.PickTimeViewModel
import kotlinx.android.synthetic.main.pick_time_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime


class PickTimeFragment : Fragment() , KodeinAware{

    private lateinit var viewModel: PickTimeViewModel

    override val kodein by closestKodein()

    private val pickTimeFactory: PickTimeFactory by instance()

    var houre: Int = 0
    var minute: Int = 0
    var date:Date? = null

    var requestId:String? = null
    private lateinit var userId:String

    lateinit var format:DateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pick_time_fragment, container, false)
    }

    @ExperimentalTime
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.title_toolbar.text = getString(R.string.pick_time)
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        (activity as MainActivity).currentPage = MainPages.PICK_TIME


        viewModel = ViewModelProviders.of(this , pickTimeFactory).get(PickTimeViewModel::class.java)

        val args = arguments?.let { PickTimeFragmentArgs.fromBundle(it) }
        requestId = args?.requestId
        userId = args?.userId!!

        format = SimpleDateFormat("dd-MM-yyyy" , Locale.US)

        pick_time.setOnClickListener {
            setTheTime()
        }

        save.setOnClickListener {
            loading.visibility = View.VISIBLE
            saveTheTime()
        }

        calendar.setOnDateChangeListener { calendarView, year, month, day ->
            val date = "$day-${month + 1}-$year"
            this.date = format.parse(date)
        }

        viewModel.hasCompletedSuccessfully.observe(this , Observer {isSuccessfull ->
            loading.visibility = View.GONE
            if (isSuccessfull){
                this.findNavController().navigateUp()
                Toast.makeText(this.context , getString(R.string.save_done) , Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this.context , getString(R.string.error) , Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun setTheTime(){
        val mTimePicker = TimePickerDialog(this.context , { timePicker, houre, minute ->
            this.houre = houre
            this.minute = minute
        } , if (houre == 0) 12 else houre, if (minute == 0) 12 else minute , true)

        mTimePicker.setTitle(getString(R.string.pick_dating_time))
        mTimePicker.show()
    }

    @ExperimentalTime
    private fun saveTheTime(){
        val calendar = if (date == null) Date(calendar.date) else date
        val now = Calendar.getInstance().time

        if (now.before(calendar) && (houre != 0 && minute != 0)){
            viewModel.saveTheTime(requestId = requestId , userId = userId , houre = houre , minute = minute , date = format.format(date).toString())
        } else {
            loading.visibility = View.GONE
            Toast.makeText(this.context , getString(R.string.choose_valid_date_and_time) , Toast.LENGTH_LONG).show()
        }

        //Log.v("koko" , format.format(date).toString())
    }



}
