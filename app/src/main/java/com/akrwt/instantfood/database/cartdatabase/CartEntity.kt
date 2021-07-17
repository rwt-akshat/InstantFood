package com.akrwt.instantfood.database.cartdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val food_id: String,
    val restaurant_id: String,
    val food_name: String,
    val foodCost:String
)