package com.hossam.hasanin.getadate.Ui.Fragments.SignupLogin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.hossam.hasanin.getadate.Externals.showTheErrors

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.ProfileSettings
import com.hossam.hasanin.getadate.ViewModels.Factories.SignupLogin.SignupFactory
import com.hossam.hasanin.getadate.ViewModels.SignupLogin.SignupViewModel
import kotlinx.android.synthetic.main.date_picker.view.*
import kotlinx.android.synthetic.main.signup_fragment.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SignupFragment : Fragment() , KodeinAware {

    override val kodein by closestKodein()

    private lateinit var viewModel: SignupViewModel

    private var gender = -1

    private var selectedGover = 0

    private val signupFactory: SignupFactory by instance()

    private var year : Int? = null
    private var month : Int? = null
    private var day : Int? = null

    private val listener by lazy {
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                p0!!.setSelection(selectedGover)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                selectedGover = position
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , signupFactory).get(SignupViewModel::class.java)

        val args = arguments?.let { SignupFragmentArgs.fromBundle(it) }
        gender = args!!.gender


        ArrayAdapter.createFromResource(context!! , R.array.governorates , android.R.layout.simple_spinner_item).let { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            governorates.apply {
                this.adapter = adapter
                this.onItemSelectedListener = listener
            }
        }

        signupbtn.setOnClickListener {
            if (accept_terms.isChecked)
                signup()
            else
                Toast.makeText(context , "مهو بص انت لازم توافق على الشروط" , Toast.LENGTH_LONG).show()
        }

        set_birth_date.setOnClickListener {
            pickDateDialog()
        }

    }

    private fun pickDateDialog(){
        val dialog = AlertDialog.Builder(activity)
        val layout = layoutInflater.inflate(R.layout.date_picker , null)
        dialog.setView(layout)
        val  ad = dialog.create()

        ArrayAdapter.createFromResource(context!! , R.array.years, android.R.layout.simple_spinner_item).let { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            layout.year.apply {
                this.adapter = adapter
                this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        p0!!.setSelection(0)
                        this@SignupFragment.year = resources.getStringArray(R.array.years)[0].toInt()
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        this@SignupFragment.year = resources.getStringArray(R.array.years)[position].toInt()
                    }

                }
            }
        }

        ArrayAdapter.createFromResource(context!! , R.array.months , android.R.layout.simple_spinner_item).let { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            layout.month.apply {
                this.adapter = adapter
                this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        p0!!.setSelection(0)
                        this@SignupFragment.month = resources.getStringArray(R.array.months)[0].toInt()
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        this@SignupFragment.month = resources.getStringArray(R.array.months)[position].toInt()
                    }

                }
            }
        }

        ArrayAdapter.createFromResource(context!! , R.array.days , android.R.layout.simple_spinner_item).let { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            layout.day.apply {
                this.adapter = adapter
                this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        p0!!.setSelection(0)
                        this@SignupFragment.day = resources.getStringArray(R.array.days)[0].toInt()
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        this@SignupFragment.day = resources.getStringArray(R.array.days)[position].toInt()
                    }

                }
            }
        }


        layout.done.setOnClickListener {
            if (this.year == null || this.month == null || this.day == null){
                Toast.makeText(activity!!.applicationContext , "بص مهو انت لازم تحدد تاريخ لانه متحددش" , Toast.LENGTH_LONG).show()
            } else {
                ad.cancel()
            }
        }
        ad.setOnCancelListener {
            set_birth_date.setText("$day/$month/$year")
            Log.v("koko" , "cancel")
        }
        ad.show()
    }

    fun signup(){
        val email: String = email_entry_signup.text.trim().toString()
        val username: String = username_entry_signup.text.trim().toString()
        val password: String = password_entry_signup.text.trim().toString()
        val address: String = address_entry_signup.text.trim().toString()
        val date = "$day/$month/$year"
        var goverName = resources.getStringArray(R.array.governorates)[selectedGover]

        viewModel.signup(email , username , address , password , gender , date , goverName) { mUser , errors ->
            if (mUser != null){
                Toast.makeText(activity!!.applicationContext , getString(R.string.signup_done) , Toast.LENGTH_LONG).show()
                val intent = Intent(activity , MainActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("new" , true)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                showTheErrors(activity!!.applicationContext , errors , viewModel.ERROR_MESSAGES)
            }
        }

    }

}
