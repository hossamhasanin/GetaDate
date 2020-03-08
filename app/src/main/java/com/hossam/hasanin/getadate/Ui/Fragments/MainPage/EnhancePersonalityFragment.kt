package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.getadate.Models.UserCharacteristic

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.EnhancePersonalityFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.EnhancePersonalityViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.enhance_personality_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class EnhancePersonalityFragment : Fragment() , KodeinAware {

    override val kodein by closestKodein()

    private val enhancePersonalityFactory:EnhancePersonalityFactory by instance()

    private lateinit var viewModel: EnhancePersonalityViewModel

    private var groupAdapter: GroupAdapter<ViewHolder>? = null

    private var items: List<CharacteristicRectItem>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.enhance_personality_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        (activity as MainActivity).currentPage = MainPages.ENHANCEMENT_PERSONALITY

        viewModel = ViewModelProviders.of(this , enhancePersonalityFactory).get(EnhancePersonalityViewModel::class.java)


        viewModel.getUserCharacteristics()

        viewModel.characteristics.observe(this , Observer {
            if (it != null){
                bindUi(it)
            } else {
                showErrorMess()
            }
            char_list.apply {
                layoutManager = LinearLayoutManager(this@EnhancePersonalityFragment.activity)
                adapter = groupAdapter
            }
        })

    }

    private fun List<UserCharacteristic>.toCharacteristicCard() : List<CharacteristicRectItem> {
        return this.map {
            CharacteristicRectItem(it)
        }
    }

    private fun bindUi(data : List<UserCharacteristic>){
        val items = data.toCharacteristicCard()
        groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        groupAdapter!!.setOnItemClickListener { item, view ->
            if (item is CharacteristicRectItem){
//                val action = EnhancePersonalityFragmentDirections.goToAddMoreQuestions(item.characteristic.getId()!! , false)
//                Navigation.findNavController(view).navigate(action)
            }
        }
    }

    private fun showErrorMess(){
        if (groupAdapter == null){
            groupAdapter = GroupAdapter<ViewHolder>().apply {
                add(ErrorCardItem("حدث خطأ في تحميل البيانات"))
            }
        } else {
            if (items != null) {
                groupAdapter!!.removeAll(items!!)
                items = null
            }
            groupAdapter!!.add(ErrorCardItem("حدث خطأ في تحميل البيانات"))
        }
    }

}
