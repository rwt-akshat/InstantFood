package com.akrwt.instantfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.utils.Orders

class OrderAdapter(val context: Context, private val orderList: ArrayList<Orders>, private val linearLayout: LinearLayout) :
    RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.order_restaurant_name)
        val orderDate: TextView = itemView.findViewById(R.id.order_date)
        val orderedFoodRecyclerView: RecyclerView =
            itemView.findViewById(R.id.ordered_food_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = orderList[position]
        holder.restaurantName.text = current.restaurant_name
        holder.orderDate.text = current.order_placed_at

        if (orderList.size == 0) {
            linearLayout.visibility = View.VISIBLE
        }else{
            linearLayout.visibility = View.GONE
        }

        holder.orderedFoodRecyclerView.setHasFixedSize(true)
        holder.orderedFoodRecyclerView.adapter =
            OrderedFoodAdapter(
                context,
                current.orderedFood
            )
    }
}