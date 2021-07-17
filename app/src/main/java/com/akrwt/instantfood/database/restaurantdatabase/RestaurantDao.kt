package com.akrwt.instantfood.database.restaurantdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRestaurant(food: RestaurantEntity)

    @Delete
    fun deleteRestaurant(food: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurant(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE restaurant_id == :restaurantId")
    fun getRestaurantById(restaurantId: String): RestaurantEntity

}