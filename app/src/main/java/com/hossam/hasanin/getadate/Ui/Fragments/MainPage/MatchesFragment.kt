package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.hossam.hasanin.getadate.Externals.getGenderReverse
import com.hossam.hasanin.getadate.Externals.getMatchList
import com.hossam.hasanin.getadate.Models.User

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.MatchedUsersAdapter
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.MatchesFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.MatchesViewModel
import kotlinx.android.synthetic.main.matches_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchesFragment : BaseFragment() , KodeinAware {

    override val kodein by closestKodein()

    private val matchesFactory:MatchesFactory by instance()

    private lateinit var viewModel: MatchesViewModel

    var matchedUsersAdapter:MatchedUsersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.matches_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        activity!!.title_toolbar.text = getString(R.string.candidates)
        (activity as MainActivity).currentPage = MainPages.MATCHES

        viewModel = ViewModelProviders.of(this , matchesFactory).get(MatchesViewModel::class.java)

        bindRec()

    }

    private fun bindRec() = launch {
        val matchedUsers = viewModel.currentUser?.getMatchList()
        if (matchedUsers!!.isNotEmpty()) {
            not_found_candidates.visibility = View.GONE
            matches_rec.visibility = View.VISIBLE

            val query = viewModel.firestore.collection("users")
                .whereEqualTo("gender", viewModel.currentUser?.getGenderReverse())
                .orderBy("username")
            val firebaseOptions = FirestoreRecyclerOptions.Builder<User?>().setQuery(query) { snapshot ->
                val user = snapshot.toObject(User::class.java)
                if (matchedUsers!!.contains(user!!.id)) {
                    return@setQuery user
                }
                return@setQuery User()
            }.build()

            matchedUsersAdapter = MatchedUsersAdapter(firebaseOptions)

            matches_rec.apply {
                adapter = matchedUsersAdapter
                layoutManager = LinearLayoutManager(this@MatchesFragment.activity)
            }
            matchedUsersAdapter!!.startListening()
        } else {
            matches_rec.visibility = View.GONE
            not_found_candidates.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if (matchedUsersAdapter != null){
            matchedUsersAdapter!!.startListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (matchedUsersAdapter != null){
            matchedUsersAdapter!!.stopListening()
        }
    }

}
