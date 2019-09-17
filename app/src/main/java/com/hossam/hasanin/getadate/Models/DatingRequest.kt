package com.hossam.hasanin.getadate.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class DatingRequest(
    var to : String? = null,
    var from: String? = null,
    var status: Int? = 0,
    var place : String? = null,
    var time: String? = null,
    var date: String? = null,
    var uids: ArrayList<String?>? = null,
    val timeAccepted: Boolean? = true,
    val placeAccepted: Boolean? = true,
    @ServerTimestamp
    val timestamp: Date? = null
) {
    private var id: String? = ""
    fun withId(id:String){
        this.id = id
    }

    fun getId():String?{
        return id
    }
}