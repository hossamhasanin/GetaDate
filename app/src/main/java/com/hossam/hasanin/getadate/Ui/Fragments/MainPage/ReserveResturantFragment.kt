package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hossam.hasanin.getadate.Models.Resturant

import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.Ui.Adapter.ResturantsAdapter
import com.hossam.hasanin.getadate.Ui.MainActivity
import com.hossam.hasanin.getadate.Ui.MainPages
import com.hossam.hasanin.getadate.ViewModels.Factories.MainPage.ReserveResturantFactory
import com.hossam.hasanin.getadate.ViewModels.MainPage.ReserveResturantViewModel
import kotlinx.android.synthetic.main.reserve_resturant_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ReserveResturantFragment : Fragment() , KodeinAware {

    override val kodein by closestKodein()

    private val reserveResturantFactory:ReserveResturantFactory by instance()

    private lateinit var viewModel: ReserveResturantViewModel
    var resturantsAdapter:ResturantsAdapter? = null

    var userId : String? = null
    var requestId : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reserve_resturant_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.title_toolbar.text = getString(R.string.choose_place)
        activity!!.left_icon.setImageResource(R.drawable.ic_arrow_back_black_24dp)
        activity!!.right_icon.visibility = View.GONE
        activity!!.left_icon.visibility = View.VISIBLE
        (activity as MainActivity).currentPage = MainPages.RESERVE_RESTURANT

        val args = arguments?.let { ReserveResturantFragmentArgs.fromBundle(it) }
        userId = args?.userId
        requestId = args?.requestId

        viewModel = ViewModelProviders.of(this , reserveResturantFactory).get(ReserveResturantViewModel::class.java)

        val query = viewModel.firestore.collection("resturants")
        val options = FirestoreRecyclerOptions.Builder<Resturant>()
            .setQuery(query){
                val resturant = it.toObject(Resturant::class.java)
                resturant?.withId(it.id)
                return@setQuery resturant!!
            }.build()
        resturantsAdapter = ResturantsAdapter(options= options , viewModel = viewModel , userId = userId!! , requestId = requestId , bar = saving_bar)


        bindRec()


        viewModel.completedSuccessFully.observe(this , Observer { isSuccessFull ->
            saving_bar.visibility = View.GONE
            if (isSuccessFull){
                Toast.makeText(this.context , getString(R.string.choosed_place_successfiully) , Toast.LENGTH_LONG).show()
                this.findNavController().navigateUp()
            } else {
                Toast.makeText(this.context , getString(R.string.error_while_choosing_the_place) , Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun bindRec(){
        resturants_rec.apply {
            layoutManager = LinearLayoutManager(this@ReserveResturantFragment.activity)
            adapter = resturantsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        if (resturantsAdapter != null){
            resturantsAdapter!!.startListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (resturantsAdapter != null){
            resturantsAdapter!!.stopListening()
        }
    }

}
