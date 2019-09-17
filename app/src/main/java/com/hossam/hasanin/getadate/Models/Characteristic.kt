package com.hossam.hasanin.getadate.Models

data class Characteristic(
    var id: String,
    var title: String,
    var question: String
) {
    constructor() : this("" , "" , "" ) {}
}