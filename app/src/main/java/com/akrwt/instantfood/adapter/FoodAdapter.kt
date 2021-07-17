package com.akrwt.instantfood.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.database.cartdatabase.CartEntity
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.utils.FoodUtil
import com.akrwt.instantfood.utils.toast

class FoodAdapter(private val context: Context, private val foodList: ArrayList<FoodUtil>) :
    RecyclerView.Adapter<FoodAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.food_name)
        val foodCost: TextView = itemView.findViewById(R.id.food_cost)
        val foodPosition: TextView = itemView.findViewById(R.id.position)
        val addBtn: Button = itemView.findViewById(R.id.add_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.food_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentFood = foodList[position]
        holder.foodPosition.text = (position + 1).toString()
        holder.foodName.text = currentFood.name
        holder.foodCost.text = "Rs. ${currentFood.cost_for_one}"

        val res = CartEntity(
            currentFood.id,
            currentFood.restaurant_id,
            currentFood.name,
            currentFood.cost_for_one
        )

        val checkExist = CartDBAsyncTask(
            context.applicationContext,
            res,
            1
        ).execute()
        val isExist = checkExist.get()
        if (isExist) {
            holder.addBtn.text = "Remove"

            holder.addBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.removeBtnColor))
        } else {
            holder.addBtn.text = "Add"
            holder.addBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
        }
        holder.addBtn.setOnClickListener {
            if (!CartDBAsyncTask(
                    context.applicationContext,
                    res,
                    1
                ).execute().get()) {
                val async = CartDBAsyncTask(
                    context.applicationContext,
                    res,
                    2
                ).execute()
                val result = async.get()
                if (result) {
                    holder.addBtn.text = "Remove"
                    holder.addBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.removeBtnColor))
                } else {
                    context.toast("Some error occured")
                }
            } else {
                val async = CartDBAsyncTask(
                    context.applicationContext,
                    res,
                    3
                ).execute()
                val result = async.get()

                if (result) {
                    holder.addBtn.text = "Add"
                    holder.addBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
                } else {
                    context.toast("Some Error Occured!!")
                }

            }
        }
    }

    class CartDBAsyncTask(
        private val context: Context,
        private val cartEntity: CartEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db = CommonDatabase.getInstance(context)

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val cart: CartEntity? =
                        db.cartDao().getItemById(cartEntity.food_id)
                    return cart != null
                }
                2 -> {
                    db.cartDao().insertItem(cartEntity)
                    return true
                }
                3 -> {
                    db.cartDao().deleteItem(cartEntity)
                    return true
                }
            }
            return false
        }
    }
}



