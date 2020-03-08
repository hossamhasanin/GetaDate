package com.hossam.hasanin.getadate.Models

data class CharacteristicQuestions(
    var title: String?,
    var cId: String?,
    var answers: ArrayList<String>? ,
    var gender: Int?,
    var selected: Int? = null,
    var loading: Boolean = false,
    var error: Throwable? = null
) {
    constructor(): this(""  , "" , null , null)
    private var id = ""

    fun withId(id:String){
        this.id = id
    }

    fun getId(): String{
        return this.id
    }
}