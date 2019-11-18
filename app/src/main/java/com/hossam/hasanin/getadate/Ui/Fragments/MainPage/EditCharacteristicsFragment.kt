package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.getadate.Models.UserCharacteristic

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.EditCharacteristicsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.EditCharacteristicsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.edit_characteristics_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class EditCharacteristicsFragment : BaseMainPageFragment() , KodeinAware {

    override val kodein by closestKodein()

    private val editCharacteristicsFactory: EditCharacteristicsFactory by instance()

    private lateinit var viewModel: EditCharacteristicsViewModel

    var groupAdapter:GroupAdapter<ViewHolder>? = null

    var cards: List<EditCharacteristicCard>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_characteristics_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this , editCharacteristicsFactory).get(EditCharacteristicsViewModel::class.java)

        viewModel.getUserCharacteristics()


        viewModel.characteristics.observe(this , Observer {
            if (it != null){
                bindUi(it)
            }
        })

        viewModel.deletingListener.observe(this , Observer {
            groupAdapter!!.remove(it)
            groupAdapter!!.notifyItemRemoved(cards!!.indexOf(it))
            launch (Dispatchers.IO){
                viewModel.deleteCharacteristic(it.data.getId()!!)
            }
        })

        viewModel.errorMess.observe(this , Observer {
            Toast.makeText(this.activity , it , Toast.LENGTH_SHORT).show()
        })
    }

    private fun bindUi(data : List<UserCharacteristic>){
        cards = data.map {
            EditCharacteristicCard(it , viewModel)
        }
        groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(cards!!)
        }
        characteristics.apply {
            layoutManager = LinearLayoutManager(this@EditCharacteristicsFragment.activity)
            adapter = groupAdapter
        }
    }

}
