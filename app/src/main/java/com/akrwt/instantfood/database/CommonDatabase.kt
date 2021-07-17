package com.akrwt.instantfood.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akrwt.instantfood.database.cartdatabase.CartDao
import com.akrwt.instantfood.database.cartdatabase.CartEntity
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantDao
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantEntity

@Database(
    entities = [RestaurantEntity::class, CartEntity::class],
    version = 1
)
abstract class CommonDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun cartDao():CartDao

    companion object {

        @Volatile
        private var instance: CommonDatabase? = null

        fun getInstance(context: Context): CommonDatabase = instance
            ?: synchronized(this) {
            instance
                ?: buildDatabase(
                    context
                )
                    .also { instance = it }
        }

        private fun buildDatabase(context: Context): CommonDatabase {
            return Room.databaseBuilder(context, CommonDatabase::class.java, "AppDatabase").build()
        }
    }
}