package com.hossam.hasanin.getadate.Ui.Fragments.ProfileSettings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.CharacteristicsAdapter
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.ProfileSettings.CharacteristicsFactory
import com.hossam.hasanin.getadate.ViewModels.ProfileSettings.CharacteristicsViewModel
import kotlinx.android.synthetic.main.characteristics_fragment.*
import kotlinx.android.synthetic.main.main_informations_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance


class CharacteristicsFragment : BaseFragment() , KodeinAware {

    override val kodein by closestKodein()

    val characteristicsAdapter: ((FirestoreRecyclerOptions<Characteristic?>) -> CharacteristicsAdapter) by factory()

    val characteristicsFactory: CharacteristicsFactory by instance()

    private lateinit var viewModel: CharacteristicsViewModel

    private lateinit var adapter: CharacteristicsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.characteristics_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , characteristicsFactory).get(CharacteristicsViewModel::class.java)

        val args = arguments?.let { CharacteristicsFragmentArgs.fromBundle(it) }
        val isNew = args!!.isNew

        if (!isNew){
            (activity as MainActivity).currentPage = MainPages.EDIT_CHARACTERISTICS
            activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
            activity!!.right_icon.visibility = View.GONE
            activity!!.left_icon.visibility = View.VISIBLE
            activity!!.title_toolbar.text = getString(R.string.edit_characteristics)

            viewModel.getCharacteristics()
        }

        val query: Query = viewModel.fireStore
            .collection("characteristics")

        val options: FirestoreRecyclerOptions<Characteristic?> =
            FirestoreRecyclerOptions.Builder<Characteristic?>()
                .setQuery(query) { snapshot ->
                    val characteristic : Characteristic? = snapshot.toObject(Characteristic::class.java)
                    characteristic?.id = snapshot.id

                    characteristic!!
                }
                .build()

        adapter = characteristicsAdapter(options)

        characteristics.adapter = adapter
        characteristics.layoutManager = LinearLayoutManager(activity)

        save_characteristics.setOnClickListener {
            save_characteristics.isClickable = false
            saving_charec.visibility = View.VISIBLE
            saveCharacteristics()
            Log.v("focus" , adapter.finalData.toString())
        }

        viewModel.characteristics.observe(this , Observer {
            adapter.finalData = it
            adapter.notifyDataSetChanged()
        })

        viewModel.finshedSaving.observe(this , Observer { finished ->
            if (finished){
                Toast.makeText(activity , "تم الحفظ" , Toast.LENGTH_LONG).show()
                if (isNew){
                    startActivity(Intent(activity, MainActivity::class.java))
                } else {
                    findNavController().navigateUp()
                }
            } else {
                profile_save.isClickable = true
                Toast.makeText(activity , viewModel.errorMess , Toast.LENGTH_LONG).show()
            }
        })


    }

    fun saveCharacteristics() = launch (Dispatchers.IO){
        val data = adapter.finalData
        Log.v("koko" , data.toString())
        viewModel.saveIntoFireStore(data , adapter.deleted)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

}
