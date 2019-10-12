package com.hossam.hasanin.getadate.Models

data class Characteristic(
    var id: String?,
    override var title: String?,
    var gender: Int?,
    override var order: Int?
) : CharacteristicsInterface {

    constructor() : this("" , "" , null , null);

}