package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.AnswersItem
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.AnswerQuestionsFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.AnswerQuestionsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.answer_questions_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AnswerQuestionsFragment : BaseFragment() , KodeinAware{

    override val kodein by closestKodein()

    private val answerQuestionsFactory:AnswerQuestionsFactory by instance()

    private lateinit var viewModel: AnswerQuestionsViewModel

    var groupAdapter: GroupAdapter<ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.answer_questions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).currentPage = MainPages.ANSWER_QUESTIIONS
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE

        val args = arguments?.let { AnswerQuestionsFragmentArgs.fromBundle(it) }

        viewModel = ViewModelProviders.of(this , answerQuestionsFactory).get(AnswerQuestionsViewModel::class.java)
        viewModel.edit = args!!.edit

        launch(IO) {
            viewModel.getQuestions()
        }

        viewModel.viewItems.observe(this , Observer {
            Log.v("koko" , it.toString())
            if (it.loading){
                loading()
            } else if (it.error != null){
                error(it.error!!.localizedMessage)
            } else {
                loadTheData(it)
            }
        })

    }

    private fun loadTheData(data: CharacteristicQuestions){
        loading.visibility = View.GONE
        question.visibility = View.VISIBLE
        answers.visibility = View.VISIBLE

        question.text = data.title

        groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(data.answers!!.toAnswerItems(data.selected))
        }

        answers.apply {
            layoutManager = LinearLayoutManager(this@AnswerQuestionsFragment.activity)
            adapter = groupAdapter
        }

    }

    private fun loading(){
        loading.visibility = View.VISIBLE
        //question.visibility = View.GONE
        answers.visibility = View.GONE
    }

    private fun error(mess: String){
        loading.visibility = View.GONE
        question.visibility = View.VISIBLE
        answers.visibility = View.GONE
        question.text = mess
    }

    fun ArrayList<String>.toAnswerItems(selectedIndex: Int?) : List<AnswersItem>{
        var c = -1
        var selcted:Boolean = false
        return this.map {
            c++
            if (selectedIndex != null) selcted = c == selectedIndex
            AnswersItem(it , viewModel , selcted)
        }
    }

}
