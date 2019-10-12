package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import com.hossam.hasanin.getadate.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.error_message.view.*

class ErrorCardItem(val mess: String) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.mess.text = mess
    }

    override fun getLayout() = R.layout.error_message
}