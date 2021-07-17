package com.akrwt.instantfood.fragments.main

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.adapter.FavAdapter
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.database.restaurantdatabase.RestaurantEntity
import kotlinx.android.synthetic.main.fragment_favourite.view.*

class FavouriteFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_favourite, container, false)

        val recyclerFav: RecyclerView = v.findViewById(R.id.recycler_favs)
        recyclerFav.layoutManager = LinearLayoutManager(requireContext())

        val dbRestaurantList = RetrieveFavourites(
            requireContext()
        ).execute().get()

        v.fav_add_btn.setOnClickListener {
            findNavController().navigate(R.id.action_favourite_to_home)
        }


        if (context != null) {

            if (dbRestaurantList.isEmpty()) {
                v.ll_layout.visibility = View.VISIBLE

            } else {
                v.ll_layout.visibility = View.GONE
                val adapter = FavAdapter(
                    requireContext(),
                    dbRestaurantList,
                    v.ll_layout
                )
                recyclerFav.adapter = adapter
            }
        }


        return v
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = CommonDatabase.getInstance(context)
            return db.restaurantDao().getAllRestaurant()
        }
    }
}