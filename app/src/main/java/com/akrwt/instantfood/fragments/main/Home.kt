package com.akrwt.instantfood.fragments.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.activity.MainActivity
import com.akrwt.instantfood.R
import com.akrwt.instantfood.adapter.RestaurantsAdapter
import com.akrwt.instantfood.utils.RestaurantsUtil
import com.akrwt.instantfood.utils.checkConnectivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var restaurantsAdapter: RestaurantsAdapter
    private lateinit var foodList: ArrayList<RestaurantsUtil>
    private var costComparator = Comparator<RestaurantsUtil> { food1, food2 ->
        if (food1.cost_for_one.compareTo(food2.cost_for_one, true) == 0) {
            food1.rating.compareTo(food2.rating, true)
        } else {
            food1.cost_for_one.compareTo(food2.cost_for_one, true)
        }
    }
    private var ratingComparator = Comparator<RestaurantsUtil> { food1, food2 ->
        if (food1.rating.compareTo(food2.rating, true) == 0) {
            food1.cost_for_one.compareTo(food2.cost_for_one, true)
        } else {
            food1.rating.compareTo(food2.rating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerView = v.findViewById(R.id.recycler_restaurants)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        foodList = ArrayList()

        if (checkConnectivity(requireContext()))
            getRestaurants()
        else {
            AlertDialog.Builder(context).apply {
                setTitle("Error")
                    .setMessage("Internet Connection is not found")
                setPositiveButton("Open Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    MainActivity().finish()
                }
                setNegativeButton("Exit") { _, _ ->
                    MainActivity().finishAffinity()
                }
                create()
                show()
            }
        }

        return v
    }

    private fun getRestaurants() {
        val jsonReq = object : JsonObjectRequest(
            Method.GET,
            "http://13.235.250.119/v2/restaurants/fetch_result/",
            null,
            Response.Listener {
                if (it.getJSONObject("data").getBoolean("success")) {
                    val jsonArray = it.getJSONObject("data").getJSONArray("data")
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        foodList.add(
                            RestaurantsUtil(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("rating"),
                                obj.getString("cost_for_one"),
                                obj.getString("image_url")
                            )
                        )
                    }
                    restaurantsAdapter =
                        RestaurantsAdapter(
                            requireContext(),
                            foodList
                        )
                    recyclerView.adapter = restaurantsAdapter
                }
            },
            Response.ErrorListener {
                Log.e("response", it.message!!)
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["Content-type"] = "application/json"
                hashMap["token"] = "e3a2d90b54bcd0"
                return hashMap
            }
        }
        Volley.newRequestQueue(context).add(jsonReq)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_high -> {
                Collections.sort(foodList, costComparator)
                foodList.reverse()
            }
            R.id.sort_low -> {
                Collections.sort(foodList, costComparator)
            }
            R.id.sort_rating -> {
                Collections.sort(foodList, ratingComparator)
                foodList.reverse()
            }
        }
        restaurantsAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}