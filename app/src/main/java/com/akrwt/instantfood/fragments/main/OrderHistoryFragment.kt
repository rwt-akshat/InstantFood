package com.akrwt.instantfood.fragments.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.activity.MainActivity
import com.akrwt.instantfood.R
import com.akrwt.instantfood.adapter.OrderAdapter
import com.akrwt.instantfood.utils.OrderedFood
import com.akrwt.instantfood.utils.Orders
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.utils.toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_order_history.*
import kotlinx.android.synthetic.main.fragment_order_history.view.*

class OrderHistoryFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    private lateinit var sharedPref: SharedPreferences
    lateinit var orderList: ArrayList<Orders>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerView = v.findViewById(R.id.order_recycler_view)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, layoutManager.orientation))

        orderList = ArrayList()

        sharedPref = requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", "def")

        val linearL:LinearLayout = v.findViewById(R.id.ll_order_layout)

        v.order_btn.setOnClickListener {
            findNavController().navigate(R.id.action_orderHistory_to_home)
        }

        if(checkConnectivity(requireContext()))
        getOrders(linearL,userId)
        else{
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

    private fun getOrders(linearLayout: LinearLayout, userID:String?) {

        val jsonObjReq = object :
            JsonObjectRequest(Method.GET,
                "http://13.235.250.119/v2/orders/fetch_result/$userID",
                null,
                Response.Listener {
                    val obj = it.getJSONObject("data")
                    if (obj.getBoolean("success")) {
                        val jsonArr = obj.getJSONArray("data")
                        for (i in 0 until jsonArr.length()) {
                            val foodItems = jsonArr.getJSONObject(i).getJSONArray("food_items")
                            val orderedFood: ArrayList<OrderedFood> = ArrayList()
                            for (j in 0 until foodItems.length()) {
                                orderedFood.add(
                                    OrderedFood(
                                        foodItems.getJSONObject(j).getString("name"),
                                        foodItems.getJSONObject(j).getString("cost")
                                    )
                                )
                            }
                            orderList.add(
                                Orders(
                                    jsonArr.getJSONObject(i).getString("restaurant_name"),
                                    jsonArr.getJSONObject(i).getString("order_placed_at"),
                                    orderedFood
                                )
                            )
                        }
                        val adapter = OrderAdapter(
                            requireContext(),
                            orderList,
                            linearLayout
                        )
                        recyclerView.adapter = adapter





                    } else {
                        requireContext().toast("Something happened")
                    }
                },
                Response.ErrorListener {
                    requireContext().toast("Error: " + it.message)
                }
            ) {
            override fun getHeaders(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["Content-type"] = "application/json"
                hashMap["token"] = "e3a2d90b54bcd0"
                return hashMap
            }
        }
        Volley.newRequestQueue(context).add(jsonObjReq)
    }
}