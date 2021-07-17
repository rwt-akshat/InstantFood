package com.akrwt.instantfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.utils.OrderedFood

class OrderedFoodAdapter(
    val context: Context,
    private val orderedFoodList: ArrayList<OrderedFood>
) :
    RecyclerView.Adapter<OrderedFoodAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.ordered_food_name)
        val cost: TextView = itemView.findViewById(R.id.ordered_food_cost)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.ordered_food_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderedFoodList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = orderedFoodList[position]
        holder.name.text = current.name
        holder.cost.text = "Rs. ${current.cost}"

    }
}