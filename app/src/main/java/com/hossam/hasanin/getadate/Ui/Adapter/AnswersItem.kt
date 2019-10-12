package com.hossam.hasanin.getadate.Ui.Adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.answer_card.view.*

class AnswersItem(val answer: String , var selected: Boolean) : Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.answer.text = answer
        if (selected){
            viewHolder.itemView.answer.background = ContextCompat.getDrawable(viewHolder.itemView.context , R.drawable.color_accent_tag_box)
            viewHolder.itemView.answer.setTextColor(Color.WHITE)
        } else {
            viewHolder.itemView.answer.background = ContextCompat.getDrawable(viewHolder.itemView.context , R.drawable.gray_tag_box)
            viewHolder.itemView.answer.setTextColor(Color.BLACK)
        }
//        viewHolder.itemView.answer.setOnClickListener {
//            viewHolder.itemView.answer.background = ContextCompat.getDrawable(it.context , R.drawable.color_accent_tag_box)
//            viewHolder.itemView.answer.setTextColor(Color.WHITE)
//            selected = true
//        }
    }

    override fun getLayout() = R.layout.answer_card

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2


}