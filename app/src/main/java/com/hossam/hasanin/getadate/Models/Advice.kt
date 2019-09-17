package com.hossam.hasanin.getadate.Models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Advice(
    val title: String? = null,
    val order: Int? = null,
    @ServerTimestamp
    val timestamp: Date? = null
)