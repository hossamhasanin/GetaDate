package com.hossam.hasanin.getadate.Models

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


data class User(
    var id: String? = null,
    var username: String? = null,
//    var firstName: String? = null,
//    var secondName: String? = null,
    var email: String? = null,
    var age: Int? = null,
    var online: Boolean? = false,
    var gender: Int? = 1,
    var governorate: String? = null,
    var personalityRate: Double? = 0.0,
    var location: ArrayList<Double?>? = null,
    var address: String? = null,
    @ServerTimestamp
    val timestamp: Date? = null
) {
    fun toHashmap() : HashMap<String , Any?>{
        return hashMapOf(
            "id" to id ,
            "username" to username,
//            "firstName" to firstName,
            "address" to address,
            "email" to email,
            "governorate" to governorate,
            "age" to age,
            "online" to online,
            "gender" to gender,
            "personalityRate" to personalityRate,
            "location" to location,
            "timestamp" to timestamp
        )
    }
}