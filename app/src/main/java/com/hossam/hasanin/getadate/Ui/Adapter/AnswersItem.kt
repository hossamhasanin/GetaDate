package com.hossam.hasanin.getadate.Ui.Adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.MainPage.AnswerQuestionsViewModel
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.answer_card.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class AnswersItem(val answer: String , val viewModel: AnswerQuestionsViewModel , val selecdted:Boolean) : Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.answer.text = answer

        if (selecdted){
            viewHolder.itemView.answer.background = ContextCompat.getDrawable(viewHolder.itemView.context , R.drawable.color_accent_tag_box)
        } else {
            viewHolder.itemView.answer.background = ContextCompat.getDrawable(viewHolder.itemView.context , R.drawable.non_selected_tag_box)
        }
        viewHolder.itemView.answer.setTextColor(Color.WHITE)

        viewHolder.itemView.answer.setOnClickListener {
            viewHolder.itemView.answer.background = ContextCompat.getDrawable(viewHolder.itemView.context , R.drawable.color_accent_tag_box)
            val job = CoroutineScope(IO).launch {
                viewModel.submitTheAnswer(this@AnswersItem)
                withContext(Main){
                    delay(500)
                    viewModel.goToNextQuestion()
                }
            }
            job.invokeOnCompletion {
                job.cancel()
            }
        }
    }

    override fun getLayout() = R.layout.answer_card

   // override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2


}