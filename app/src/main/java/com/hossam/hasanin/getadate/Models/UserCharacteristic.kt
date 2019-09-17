package com.hossam.hasanin.getadate.Models

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.HashMap

data class UserCharacteristic(
    var title: String?,
    var question: String?,
    var degree: Int?,
    var answer: String? ,
    @ServerTimestamp
    val timestamp: Date? = null
) {

    constructor():this("" , "" , null , "")

    fun toHashmap() : HashMap<String , Any?>{
        return hashMapOf(
            "title" to title ,
            "question" to question,
            "degree" to degree,
            "answer" to answer,
            "timestamp" to timestamp
        )
    }
}