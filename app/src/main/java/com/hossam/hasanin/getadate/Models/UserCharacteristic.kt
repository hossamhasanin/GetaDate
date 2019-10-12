package com.hossam.hasanin.getadate.Models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.HashMap

data class UserCharacteristic(
    override var title: String?,
    var degree: Int?,
    override var order: Int?,
    @ServerTimestamp
    var timestamp: Date? = null
) : CharacteristicsInterface {

    constructor(): this("" , null , null)

    private var id:String? = null

    fun getId() : String?{
        return this.id
    }

    fun withId(id : String){
        this.id = id
    }

    fun toHashmap() : HashMap<String , Any?>{
        return hashMapOf(
            "title" to title ,
            "degree" to degree,
            "order" to order,
            "timestamp" to timestamp
        )
    }
}