package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.AnswersItem
import com.hossam.hasanin.getadate.ViewModels.MainPage.AddMoreQuestionsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.add_more_questions_card.view.*

class AddMoreQuestionsCardItem(val data: CharacteristicQuestions , val viewModel:AddMoreQuestionsViewModel) : Item() {
    var selectedItem:Int? = null
    var question: UserCharacteristicQuestion? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.question.text = data.title

        val answersCards = data.answers!!.map {
            AnswersItem(it , false)
        }

        bindAnswersList(viewHolder , answersCards)

        viewHolder.itemView.save.setOnClickListener {
            if (selectedItem != null){
                viewHolder.itemView.save.visibility = View.GONE
                viewHolder.itemView.delete.visibility = View.VISIBLE

                question = UserCharacteristicQuestion(data.title , data.cId , answersCards[selectedItem!!].answer)
                question!!.withId(data.getId())
                viewModel.saveTheQuestion(question!!)
            } else {
                Toast.makeText(viewHolder.itemView.context , "اختر اجابة" , Toast.LENGTH_SHORT).show()
            }
        }

        viewHolder.itemView.delete.setOnClickListener {
            if (question != null){
                viewHolder.itemView.save.visibility = View.VISIBLE
                viewHolder.itemView.delete.visibility = View.GONE

                viewModel.deleteTheQuestion(question!!.getId())
            } else {
                Toast.makeText(viewHolder.itemView.context , "في حاجة غلط باين" , Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun bindAnswersList(viewHolder: ViewHolder , answersCards: List<AnswersItem>){

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 2
            addAll(answersCards)
        }

        viewHolder.itemView.answers.apply {
            layoutManager = GridLayoutManager(viewHolder.itemView.context , groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            (item as? AnswersItem)?.let {
                if (selectedItem != null){
                    (groupAdapter.getItem(selectedItem!!) as AnswersItem).let { k->
                        k.selected = false
                        k.notifyChanged()
                    }
                    groupAdapter.notifyItemChanged(selectedItem!!)
                }
                selectedItem = answersCards.indexOf(item)
                it.selected = true
                it.notifyChanged()
            }
        }

    }

    override fun getLayout() = R.layout.add_more_questions_card

}