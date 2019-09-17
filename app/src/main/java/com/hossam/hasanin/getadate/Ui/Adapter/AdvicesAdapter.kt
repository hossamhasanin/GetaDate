package com.hossam.hasanin.getadate.Ui.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hossam.hasanin.getadate.Models.Advice
import com.hossam.hasanin.getadate.R

class AdvicesAdapter(options: FirestoreRecyclerOptions<Advice>) : FirestoreRecyclerAdapter<Advice , AdvicesAdapter.Companion.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.advices_card , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, advice: Advice) {
        holder.advice.text = advice.title
    }

    companion object {
        class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
            val advice = item.findViewById<TextView>(R.id.advices)
        }
    }
}