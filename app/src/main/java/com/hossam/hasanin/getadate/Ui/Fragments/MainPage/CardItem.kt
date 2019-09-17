package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.user_card.*

class CardItem(private val characteristic: UserCharacteristic) : Item() {
    override fun getLayout() = R.layout.user_card

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.characteristic.text = characteristic.title
        viewHolder.score.text = characteristic.degree.toString() + "/10"
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}