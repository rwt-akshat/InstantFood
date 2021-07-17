package com.akrwt.instantfood.database.restaurantdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey
    val restaurant_id: String,
    val restaurant_name: String,
    val restaurant_rating: String,
    val restaurant_cost_for_one: String,
    val restaurantImage: String
)