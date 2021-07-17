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
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantEntity
import com.akrwt.instantfood.activity.RestaurantsDetailsActivity
import com.akrwt.instantfood.utils.RestaurantsUtil
import com.akrwt.instantfood.utils.toast
import com.squareup.picasso.Picasso

class RestaurantsAdapter(
    private val context: Context,
    private val restaurantList: ArrayList<RestaurantsUtil>
) :
    RecyclerView.Adapter<RestaurantsAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resImage: ImageView = itemView.findViewById(R.id.restaurant_image)
        val resName: TextView = itemView.findViewById(R.id.restaurant_name)
        val resCost: TextView = itemView.findViewById(R.id.restaurant_price)
        val resRating: TextView = itemView.findViewById(R.id.restaurant_rating)
        val llLayout:LinearLayout = itemView.findViewById(R.id.ll_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRestaurant = restaurantList[position]
        holder.resName.text = currentRestaurant.name
        holder.resCost.text = "₹ ${currentRestaurant.cost_for_one}/person"
        holder.resRating.text = currentRestaurant.rating
        Picasso.get().load(currentRestaurant.image_url).into(holder.resImage)

        val res =
            RestaurantEntity(
                currentRestaurant.id,
                currentRestaurant.name,
                currentRestaurant.rating,
                currentRestaurant.cost_for_one,
                currentRestaurant.image_url
            )

        val checkFav = DBAsyncTask(
            context.applicationContext,
            res,
            1
        ).execute()
        val isFav = checkFav.get()


        holder.llLayout.setOnClickListener {
            val intent = Intent(context, RestaurantsDetailsActivity::class.java)
            intent.putExtra("res_id", currentRestaurant.id)
            intent.putExtra("restaurant_name", currentRestaurant.name)
            context.startActivity(intent)
        }

        if(isFav){
            holder.resRating.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav_fill, 0, 0)
        }else{
            holder.resRating.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_fav_not_fill, 0, 0)
        }
        holder.resRating.setOnClickListener {
            if(!DBAsyncTask(
                    context.applicationContext,
                    res,
                    1
                ).execute().get()){
                val async = DBAsyncTask(
                    context.applicationContext,
                    res,
                    2
                ).execute()
                val result = async.get()
                if(result){
                    context.toast("Restaurant added to favourites")
                    holder.resRating.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_fav_fill, 0, 0)
                }else{
                    context.toast("Restaurant error occurred")
                }
            }else{
                val async = DBAsyncTask(
                    context.applicationContext,
                    res,
                    3
                ).execute()
                val result = async.get()

                if(result){
                    context.toast("Restaurant removed from favourites")
                    holder.resRating.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_fav_not_fill, 0, 0)
                }else{
                    context.toast("Some Error Occurred!!")
                }
            }
        }
    }

    class DBAsyncTask(
        private val context: Context,
        private val restaurantEntity: RestaurantEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db = CommonDatabase.getInstance(context)

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id)
                    return book != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    return true
                }
            }
            return false
        }
    }
}