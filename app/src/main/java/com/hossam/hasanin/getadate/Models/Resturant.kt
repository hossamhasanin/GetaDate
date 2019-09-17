package com.hossam.hasanin.getadate.Models

data class Resturant(
    val name: String? = null,
    val image: String? = null,
    val location: ArrayList<Double?>? = null,
    val address: String? = null
) {
    private var id:String? = ""
    fun withId(id:String?){
        this.id = id
    }

    fun getId() : String?{
        return id
    }
}