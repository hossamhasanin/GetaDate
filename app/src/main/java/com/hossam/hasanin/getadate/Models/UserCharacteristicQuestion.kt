package com.hossam.hasanin.getadate.Models

data class UserCharacteristicQuestion(
    var title: String?,
    var cId: String?,
    var answer: String?
) {
    private var id = ""
    constructor(): this("" , "" , "")

    fun withId(id:String){
        this.id = id
    }

    fun getId(): String{
        return this.id
    }

    fun toHashmap() : HashMap<String , Any?>{
        return hashMapOf(
            "title" to title ,
            "cId" to cId ,
            "answer" to answer
        )
    }
}