package com.akrwt.instantfood.utils

data class Orders(
    val restaurant_name:String,
    val order_placed_at:String,
    val orderedFood: ArrayList<OrderedFood>
)