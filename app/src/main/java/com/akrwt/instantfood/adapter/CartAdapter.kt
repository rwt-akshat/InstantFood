package com.akrwt.instantfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.database.cartdatabase.CartEntity

class CartAdapter(
    private val context: Context,
    private val cartList: List<CartEntity>
) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartFoodName: TextView = itemView.findViewById(R.id.cart_food_name)
        val cartCost: TextView = itemView.findViewById(R.id.cart_cost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cart = cartList[position]
        holder.cartFoodName.text = cart.food_name
        holder.cartCost.text = "Rs. ${cart.foodCost}"
    }
}