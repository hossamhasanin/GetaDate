package com.hossam.hasanin.getadate.Ui.Adapter

import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hossam.hasanin.getadate.Externals.getLocation
import com.hossam.hasanin.getadate.Models.Resturant
import com.hossam.hasanin.getadate.R
import com.hossam.hasanin.getadate.ViewModels.MainPage.ReserveResturantViewModel
import kotlinx.coroutines.*

class ResturantsAdapter(options:FirestoreRecyclerOptions<Resturant> , val viewModel: ReserveResturantViewModel , val userId: String , val requestId: String? , val bar : ProgressBar) : FirestoreRecyclerAdapter<Resturant , ResturantsAdapter.Companion.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resturant_card , parent , false)
        return ViewHolder(view)
    }

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int, resturant: Resturant) {
        Glide.with(holder.image.context).load(resturant.image!!.toUri()).into(holder.image)
        holder.name.text = resturant.name
        holder.address.text = resturant.address
        setCalculatedDistance(holder.distance , resturant.location)

        holder.container.setOnClickListener {
            bar.visibility = View.VISIBLE
            viewModel.saveDatingRequestPlaceDetail(requestId  = requestId, userId = userId , placeId = resturant.getId())
        }

        holder.map.setOnClickListener {
            val geoLocation = "geo:0,0?q=${resturant.location?.get(1)},${resturant.location?.get(0)}(${resturant.name})"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = geoLocation.toUri()
            }
            val packManger = holder.distance.context.packageManager
            if (intent.resolveActivity(packManger) != null){
                holder.distance.context.startActivity(intent)
            }
        }

    }

    @ExperimentalCoroutinesApi
    private fun setCalculatedDistance(view: TextView, location:ArrayList<Double?>?){
        CoroutineScope(Dispatchers.Main).launch {
            val resturantLoc = Location("")
            resturantLoc.longitude = location?.get(0)!!
            resturantLoc.latitude = location.get(1)!!

            val currentUserLocationArray = viewModel.currentUser?.getLocation()
            val currentUserLocation = Location("")
            currentUserLocation.longitude = currentUserLocationArray!![0]!!
            currentUserLocation.latitude = currentUserLocationArray[1]!!

            val calculateDistance = currentUserLocation.distanceTo(resturantLoc)
            view.text = view.context.getString(R.string.parameter_distance , String.format("%.2f", calculateDistance))

            this.cancel()
        }
    }


    companion object{
        class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
            val image = item.findViewById<ImageView>(R.id.resturant_image)
            val name = item.findViewById<TextView>(R.id.resturant_name)
            val address = item.findViewById<TextView>(R.id.address)
            val distance = item.findViewById<TextView>(R.id.distance)
            val container = item.findViewById<ConstraintLayout>(R.id.container)
            val map = item.findViewById<Button>(R.id.open_map)
        }
    }
}