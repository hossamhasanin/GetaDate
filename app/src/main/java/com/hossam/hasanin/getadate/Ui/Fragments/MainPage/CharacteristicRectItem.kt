package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.characteristic_rect_box.view.*

class CharacteristicRectItem(val characteristic: UserCharacteristic) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.title.text = characteristic.title
    }

    override fun getLayout() = R.layout.characteristic_rect_box


}