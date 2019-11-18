package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.MainPage.EditCharacteristicsViewModel
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.edit_characteristics_card.view.*

class EditCharacteristicCard(val data: UserCharacteristic , val viewModel: EditCharacteristicsViewModel) : Item() {
    var opened = false
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.title.text = data.title
        viewHolder.itemView.degree.setText(data.degree.toString())

        viewHolder.itemView.title.setOnClickListener {
            if (opened){
                opened = false
                viewHolder.itemView.details.visibility = View.GONE
            } else {
                opened = true
                viewHolder.itemView.details.visibility = View.VISIBLE
            }
        }

        viewHolder.itemView.save.setOnClickListener {
            viewHolder.itemView.save.isClickable = false
            data.degree = viewHolder.itemView.degree.text.toString().toInt()
            viewModel.saveChanges(data).addOnCompleteListener {
                viewHolder.itemView.save.isClickable = true
                Toast.makeText(viewHolder.itemView.context , "تم الحفظ" , Toast.LENGTH_SHORT).show()
            }
        }

        viewHolder.itemView.delete.setOnClickListener {
            viewModel.deletingListener.postValue(this)
        }

        viewHolder.itemView.edit_char.setOnClickListener {
            val action = EditCharacteristicsFragmentDirections.goToEditQuestions(data.getId()!! , true)
            Navigation.findNavController(it).navigate(action)
        }

    }

    override fun getLayout() = R.layout.edit_characteristics_card

}