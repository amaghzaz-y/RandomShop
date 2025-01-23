package com.amaghzaz.randomshop.models

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    val category: String,
    val description: String,
    var rating: Rating
)

data class Rating(
    val rate:Double,
    val count:Int,
)
