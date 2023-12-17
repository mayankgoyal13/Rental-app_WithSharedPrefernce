package com.example.rental_app

import java.io.Serializable

class Listings(val owner:User, var propertType:String,
               var bedrooms:Int,var kitchen:Int,
               var bathroom: Double,var description: String,
               var address:String,
               var isAvailable:Boolean,var buildingName:String,
               var postalCode:String,
               var province:String,var city:String,var img:String, var rent:Int){

}