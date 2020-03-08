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
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.Models.Advice

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.AdvicesAdapter
import com.hossam.hasanin.getadate.Ui.Fragments.BaseFragment
import com.hossam.hasanin.getadate.Ui.Fragments.BaseMainPageFragment
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.AdvicesFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.AdvicesViewModel
import kotlinx.android.synthetic.main.advices_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class AdvicesFragment : BaseMainPageFragment() , KodeinAware {

    private lateinit var viewModel: AdvicesViewModel

    override val kodein by closestKodein()

    private val advicesFactory:AdvicesFactory by instance()

    private var advicesAdapter:AdvicesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.advices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).currentPage = MainPages.ADVICES
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE

        viewModel = ViewModelProviders.of(this , advicesFactory).get(AdvicesViewModel::class.java)

        bindRec()


    }

    private fun bindRec() = launch {
        val query = viewModel.firestore.collection("advices").whereEqualTo("gender" , viewModel.mAuth.currentUser!!.getGender()).orderBy("order")
        val options = FirestoreRecyclerOptions.Builder<Advice>().setQuery(query , Advice::class.java).build()
        advicesAdapter = AdvicesAdapter(options)

        advices_rec.apply {
            layoutManager = LinearLayoutManager(this@AdvicesFragment.context)
            adapter = advicesAdapter
        }

        advicesAdapter?.startListening()

    }

    override fun onResume() {
        super.onResume()
        if (advicesAdapter != null) {
            advicesAdapter?.startListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (advicesAdapter != null) {
            advicesAdapter?.stopListening()
        }
    }


}
