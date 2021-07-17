package com.akrwt.instantfood.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.activity.RestaurantsDetailsActivity
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantEntity
import com.akrwt.instantfood.utils.toast
import com.squareup.picasso.Picasso

class FavAdapter(
    private val context: Context,
    private var favList: List<RestaurantEntity>,
    private var ll_layout: LinearLayout
) :
    RecyclerView.Adapter<FavAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resImage: ImageView = itemView.findViewById(R.id.fav_restaurant_image)
        val resName: TextView = itemView.findViewById(R.id.fav_restaurant_name)
        val resCost: TextView = itemView.findViewById(R.id.fav_restaurant_price)
        val resRating: TextView = itemView.findViewById(R.id.fav_restaurant_rating)
        val fav_ll_layout:LinearLayout = itemView.findViewById(R.id.fav_ll_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.fav_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRestaurant = favList[position]
        holder.resName.text = currentRestaurant.restaurant_name
        holder.resCost.text = "₹ ${currentRestaurant.restaurant_cost_for_one}/person"
        holder.resRating.text = currentRestaurant.restaurant_rating
        Picasso.get().load(currentRestaurant.restaurantImage).into(holder.resImage)

        holder.fav_ll_layout.setOnClickListener {
            val intent = Intent(context, RestaurantsDetailsActivity::class.java)
            intent.putExtra("res_id", currentRestaurant.restaurant_id)
            intent.putExtra("restaurant_name", currentRestaurant.restaurant_name)
            context.startActivity(intent)
        }

        val res =
            RestaurantEntity(
                currentRestaurant.restaurant_id,
                currentRestaurant.restaurant_name,
                currentRestaurant.restaurant_rating,
                currentRestaurant.restaurant_cost_for_one,
                currentRestaurant.restaurantImage
            )

        holder.resRating.setOnClickListener {

            val async = DBAsyncRemoveTask(
                context.applicationContext,
                res
            ).execute()
            val result = async.get()
            if (favList.size == 1) {
                ll_layout.visibility = View.VISIBLE
            }

            if (result) {
                (favList as ArrayList<RestaurantEntity>).removeAt(holder.adapterPosition)

                notifyItemRemoved(holder.adapterPosition)
                notifyItemRangeChanged(holder.adapterPosition, favList.size)
                context.toast("Restaurant removed from favourites")
            } else {
                context.toast("Some Error Occurred!!")
            }
        }
    }

    class DBAsyncRemoveTask(
        private val context: Context,
        private val restaurantEntity: RestaurantEntity
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db = CommonDatabase.getInstance(context)
        override fun doInBackground(vararg params: Void?): Boolean {
            db.restaurantDao().deleteRestaurant(restaurantEntity)
            return true

        }
    }
}