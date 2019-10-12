package com.hossam.hasanin.getadate.Models

data class CharacteristicQuestions(
    var title: String?,
    var cId: String?,
    var answers: ArrayList<String>?
) {
    constructor(): this(""  , "" , null)
    private var id = ""

    fun withId(id:String){
        this.id = id
    }

    fun getId(): String{
        return this.id
    }
}