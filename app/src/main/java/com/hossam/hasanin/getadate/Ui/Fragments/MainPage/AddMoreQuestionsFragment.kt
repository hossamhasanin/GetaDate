package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.AddMoreQuestionsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.AddMoreQuestionsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.add_more_questions_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AddMoreQuestionsFragment : BaseMainPageFragment() , KodeinAware{

    override val kodein by closestKodein()

    private lateinit var viewModel: AddMoreQuestionsViewModel

    private val addMoreQuestionsFactory:AddMoreQuestionsFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_more_questions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        activity!!.title_toolbar.text = getString(R.string.add_to_your_personality)
        (activity as MainActivity).currentPage = MainPages.ADD_MORE_QUESTIONS

        val args = arguments?.let { AddMoreQuestionsFragmentArgs.fromBundle(it) }
        val cId = args!!.cId

        viewModel = ViewModelProviders.of(this , addMoreQuestionsFactory).get(AddMoreQuestionsViewModel::class.java)

        loading.visibility = View.VISIBLE
        viewModel.getQuestions(cId)

        viewModel.questions.observe(this , Observer {
            if (it != null){
                bindUi(it , cId)
            }
        })

    }

    private fun bindUi(questionsData : List<CharacteristicQuestions> , cId: String) = launch{
        val userQuestions = viewModel.getUserQuestions(cId).map {
            it.title
        }

        val filteredData = questionsData.filter {
            if (!userQuestions.contains(it.title)){
                return@filter true
            }
            return@filter false
        }

        val groupAdapter = if (filteredData.isNotEmpty()){
            val data = filteredData.map { chrq ->
                AddMoreQuestionsCardItem(chrq , viewModel)
            }
            GroupAdapter<ViewHolder>().apply {
                addAll(data)
            }
        } else {
            GroupAdapter<ViewHolder>().apply {
                add(ErrorCardItem("لا يوجد اسئلة"))
            }
        }

        questions.apply {
            layoutManager = LinearLayoutManager(this@AddMoreQuestionsFragment.activity)
            adapter = groupAdapter
        }

        loading.visibility = View.GONE

    }

    override fun onStop() {
        launch (Dispatchers.IO) {
            viewModel.modifyPersonalityRate()
        }
        super.onStop()
    }

    override fun onPause() {
        launch (Dispatchers.IO){
            viewModel.modifyPersonalityRate()
        }
        super.onPause()
    }

}
