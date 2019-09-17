package com.hossam.hasanin.getadate.Ui.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hossam.hasanin.getadate.Models.User
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Fragments.MainPage.MatchesFragmentDirections
import com.hossam.hasanin.getadate.Ui.Fragments.MainPage.ShowUserFragmentArgs

class MatchedUsersAdapter(options: FirestoreRecyclerOptions<User?>) : FirestoreRecyclerAdapter<User, MatchedUsersAdapter.Companion.ViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.matches_card , parent , false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, user: User) {
        if (user.username != null){
            holder.username.setText(user.username)

            holder.cardContainer.setOnClickListener {
                goToUserFragment(user.id!! , it)
            }

        } else {
            holder.username.visibility = View.GONE
            holder.constraintContainer.visibility = View.GONE
            holder.cardContainer.visibility = View.GONE
        }
    }

    private fun goToUserFragment(id:String , view:View){
        val action = MatchesFragmentDirections.goToShowUser(id)
        Navigation.findNavController(view).navigate(action)
    }


    companion object{
        class ViewHolder(val item: View) : RecyclerView.ViewHolder(item){
            val username:TextView = item.findViewById(R.id.match_username)
            val constraintContainer: ConstraintLayout = item.findViewById(R.id.const_container)
            val cardContainer: CardView = item.findViewById(R.id.card_container)
        }
    }
}