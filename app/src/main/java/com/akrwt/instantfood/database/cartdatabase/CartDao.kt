package com.akrwt.instantfood.database.cartdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantEntity

@Dao
interface CartDao {
    @Insert
    fun insertItem(item: CartEntity)

    @Delete
    fun deleteItem(item: CartEntity)

    @Query("SELECT * FROM cart")
    fun getAllItems(): List<CartEntity>

    @Query("SELECT * FROM cart WHERE food_id == :cartId")
    fun getItemById(cartId: String): CartEntity

    @Query("DELETE FROM cart")
    fun deleteTable()
}