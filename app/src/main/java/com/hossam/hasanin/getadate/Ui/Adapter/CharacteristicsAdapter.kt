package com.hossam.hasanin.getadate.Ui.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.R
import kotlinx.android.synthetic.main.characteristic_card.view.*

class CharacteristicsAdapter(options: FirestoreRecyclerOptions<Characteristic?>) : FirestoreRecyclerAdapter<Characteristic , CharacteristicsAdapter.Companion.viewHolder>(options) {

    var finalData = HashMap<String , UserCharacteristic?>()

    var deleted = HashMap<String , UserCharacteristic?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.characteristic_card , parent , false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, characteristic: Characteristic) {
        holder.characteristicTitle.text = characteristic.title
        holder.characteristicQuestion.text = characteristic.question

        if (finalData.contains(characteristic.id)){
            holder.characteristicTitle.isChecked = true
            holder.details.visibility = View.VISIBLE
            holder.questionsAnswer.setText(finalData[characteristic.id]?.answer)
            holder.characteristicDegree.setText(finalData[characteristic.id]!!.degree!!.toString())
        }

        holder.questionsAnswer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(ans: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (ans.toString().isNotEmpty()){
                    finalData[characteristic.id]?.answer = ans.toString()
                }
            }
        })
        holder.characteristicDegree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(degree: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val finalDegree = degree.toString()
                if (finalDegree.isNotEmpty() && finalDegree.toInt() in 11 downTo 0){
                    finalData[characteristic.id]?.degree = finalDegree.toInt()
                } else {
                    Toast.makeText(holder.characteristicTitle.context , "ادخل درجة بين 1 ل 10" , Toast.LENGTH_LONG).show()
                }
            }
        })

        holder.characteristicTitle.setOnCheckedChangeListener{ compoundButton: CompoundButton, checked: Boolean ->
            if (checked){
                holder.details.visibility = View.VISIBLE
                if (!finalData.contains(characteristic.id)){
                    val userCharacteristic = UserCharacteristic(characteristic.title ,
                        characteristic.question , 0 , "")
                    finalData[characteristic.id] = userCharacteristic
                }
            } else {
                holder.details.visibility = View.GONE
                if (finalData.contains(characteristic.id)){
                    deleted[characteristic.id] = finalData[characteristic.id]
                    finalData.remove(characteristic.id)
                }
            }
        }
    }

    companion object{
        class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val characteristicTitle = itemView.characteristic_title
            val details = itemView.details
            val questionsAnswer = itemView.questions_answer
            val characteristicDegree = itemView.degree
            val characteristicQuestion = itemView.question
        }
    }

}