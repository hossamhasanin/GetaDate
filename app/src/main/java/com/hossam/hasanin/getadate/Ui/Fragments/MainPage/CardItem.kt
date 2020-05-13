package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.user_card.*

class CardItem(val characteristic: UserCharacteristic , val quesNum: Int) : Item() {
    override fun getLayout() = R.layout.user_card

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.characteristic_1.text = characteristic.title
        val ratio:Double = characteristic.degree!!.toDouble() / quesNum.toDouble()
        if (ratio != 0.0 && ratio <= 0.2){
            viewHolder.heart_1.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_2.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.2 && ratio <= 0.5){
            viewHolder.heart_1.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_2.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_3.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.5 && ratio <= 0.8){
            viewHolder.heart_1.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_2.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_3.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_4.setImageResource(R.drawable.ic_favorite_pink)
        } else if (ratio > 0.8 && ratio <= 1.0){
            viewHolder.heart_1.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_2.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_3.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_4.setImageResource(R.drawable.ic_favorite_pink)
            viewHolder.heart_5.setImageResource(R.drawable.ic_favorite_pink)
        }
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}