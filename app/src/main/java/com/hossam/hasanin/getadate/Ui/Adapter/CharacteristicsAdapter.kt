package com.hossam.hasanin.getadate.Ui.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.getadate.Externals.asDeferred
import com.hossam.hasanin.getadate.Models.Characteristic
import com.hossam.hasanin.getadate.Models.CharacteristicQuestions
import com.hossam.hasanin.getadate.Models.UserCharacteristic
import com.hossam.hasanin.getadate.Models.UserCharacteristicQuestion
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.characteristic_card.view.*

class CharacteristicsAdapter(options: FirestoreRecyclerOptions<Characteristic?>) : FirestoreRecyclerAdapter<Characteristic , CharacteristicsAdapter.Companion.viewHolder>(options) {

    var userCharacteristicsMainData = HashMap<String , UserCharacteristic?>()

    var userQuestionsAnswer = HashMap<String , UserCharacteristicQuestion>()

    //private var userGender:Int = 0

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.characteristic_card , parent , false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, characteristic: Characteristic) {
        holder.characteristicTitle.text = characteristic.title
        holder.characteristicTitle.isClickable = false
        firestore.collection("questions")
            .whereEqualTo("cId" , characteristic.id).get().addOnCompleteListener { task ->
                holder.characteristicTitle.isClickable = true
                if (task.isSuccessful){
                    val questions = task.result!!.toObjects(CharacteristicQuestions::class.java)
                    if (questions.size > 0){
                        val randomQuestionIndex = (0 until questions.size).random()
                        val randomQuestion = questions[randomQuestionIndex]
                        randomQuestion.withId(task.result!!.documents[randomQuestionIndex].id)

                        //var answersCards: List<AnswersItem>? = null

                        bindUi(holder , randomQuestion , characteristic)

                        holder.characteristicDegree.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(p0: Editable?) {

                            }

                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            }

                            override fun onTextChanged(degree: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                val finalDegree = degree.toString()
                                if (finalDegree.isNotEmpty() && finalDegree.toInt() in 11 downTo 0){
                                    userCharacteristicsMainData[characteristic.id]?.degree = finalDegree.toInt()
                                } else {
                                    Toast.makeText(holder.characteristicTitle.context , "ادخل درجة بين 1 ل 10" , Toast.LENGTH_LONG).show()
                                }
                            }
                        })

                        holder.characteristicTitle.setOnCheckedChangeListener{ compoundButton: CompoundButton, checked: Boolean ->
                            if (checked){
                                holder.details.visibility = View.VISIBLE
                                if (!userCharacteristicsMainData.contains(characteristic.id)){
                                    val userCharacteristic = UserCharacteristic(characteristic.title ,
                                        0 , characteristic.order)
                                    userCharacteristicsMainData[characteristic.id.toString()] = userCharacteristic
                                }
                                if (!userQuestionsAnswer.contains(randomQuestion.getId())){
                                    val userQuestion = UserCharacteristicQuestion(randomQuestion.title , characteristic.id , "")
                                    userQuestion.withId(task.result!!.documents[randomQuestionIndex].id)
                                    userQuestionsAnswer[characteristic.id.toString()] = userQuestion
                                }
                            } else {
                                holder.details.visibility = View.GONE
                                if (userCharacteristicsMainData.contains(characteristic.id)){
                                    //deleted[characteristic.id.toString()] = userCharacteristicsMainData[characteristic.id]
                                    userCharacteristicsMainData.remove(characteristic.id)
                                }
                                if (userQuestionsAnswer.contains(randomQuestion.getId())){
                                    userQuestionsAnswer.remove(characteristic.id.toString())
                                }
                            }
                        }
                    }
                }
            }

    }

//    fun setGender(gender:Int){
//        userGender = gender
//    }

    private fun bindUi(holder: viewHolder , question: CharacteristicQuestions , characteristic: Characteristic){

        holder.characteristicQuestion.text = question.title

//        if (userCharacteristicsMainData.contains(characteristic.id)){
//            holder.characteristicTitle.isChecked = true
//            holder.details.visibility = View.VISIBLE
//            holder.characteristicDegree.setText(userCharacteristicsMainData[characteristic.id]!!.degree!!.toString())
//        }

        val answersCards = question.answers!!.map {
            //AnswersItem(it , false)
        }

//        val groupAdapter = GroupAdapter<ViewHolder>().apply {
//            spanCount = 2
//            addAll(answersCards)
//        }
//
//        groupAdapter.setOnItemClickListener { item, view ->
//            (item as? AnswersItem)?.let {
//
//                if (!userQuestionsAnswer[characteristic.id]?.answer.isNullOrEmpty()
//                    && !userQuestionsAnswer[characteristic.id]?.answer.equals(it.answer)) {
//                    val p = answersCards.filter { previosItem ->
//                        if (previosItem.answer.equals(userQuestionsAnswer[characteristic.id]?.answer)){
//                            return@filter true
//                        }
//                        return@filter false
//                    }
//                    val s = answersCards.indexOf(p[0])
//                    Log.v("koko" , s.toString())
//                    (groupAdapter.getItem(s) as AnswersItem).let { k ->
//                        //k.selected = false
//                        k.notifyChanged()
//                    }
//                    groupAdapter.notifyItemChanged(s)
//                }
//
//                userQuestionsAnswer[characteristic.id]?.answer = it.answer
//                it.selected = true
//                it.notifyChanged()
//            }
//        }

        //setTheAnswersList(holder , groupAdapter)

    }

    private fun setTheAnswersList(holder: viewHolder , groupAdapter: GroupAdapter<ViewHolder>){

        holder.answersList.apply {
            layoutManager = GridLayoutManager(holder.answersList.context , groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

    }

    companion object{
        class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val characteristicTitle = itemView.characteristic_title
            val details = itemView.details
            val characteristicDegree = itemView.degree
            val characteristicQuestion = itemView.question
            val characteristicContainer = itemView.characteristicContainer
            val linearContainer = itemView.linearContainer
            val answersList = itemView.answers_list
        }
    }

}