package com.akrwt.instantfood.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.database.cartdatabase.CartEntity
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.adapter.FoodAdapter
import com.akrwt.instantfood.utils.FoodUtil
import com.akrwt.instantfood.utils.toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_restaurants_details.*

class RestaurantsDetailsActivity : AppCompatActivity() {

    lateinit var foodList: ArrayList<FoodUtil>
    lateinit var foodAdapter: FoodAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurants_details)
        setSupportActionBar(des_toolbar)

        val restaurantName = intent.getStringExtra("restaurant_name")
        supportActionBar!!.title = restaurantName
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        foodList = ArrayList()
        recyclerView = findViewById(R.id.details_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val resId = intent.getStringExtra("res_id")

        if (checkConnectivity(this))
            getFood(resId)
        else {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                    .setMessage("Internet Connection is not found")
                setPositiveButton("Open Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    finish()
                }
                setNegativeButton("Exit") { _, _ ->
                    finishAffinity()
                }
                create()
                show()
            }
        }

        proceed_to_cart_btn.setOnClickListener {
            val cart = GetCart(
                this
            ).execute().get()
            if (cart.isNullOrEmpty()) {
                toast("Atleast select 1 item")
            } else {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getFood(foodId: String?) {
        val jsonReq = object : JsonObjectRequest(
            Method.GET,
            "http://13.235.250.119/v2/restaurants/fetch_result/$foodId",
            null,
            Response.Listener {
                if (it.getJSONObject("data").getBoolean("success")) {
                    val jsonArray = it.getJSONObject("data").getJSONArray("data")
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        foodList.add(
                            FoodUtil(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("cost_for_one"),
                                obj.getString("restaurant_id")
                            )
                        )
                    }
                    foodAdapter =
                        FoodAdapter(
                            this,
                            foodList
                        )
                    recyclerView.adapter = foodAdapter
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
        Volley.newRequestQueue(this).add(jsonReq)
    }

    class GetCart(val context: Context) : AsyncTask<Void, Void, List<CartEntity>>() {
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val db = CommonDatabase.getInstance(context)
            return db.cartDao().getAllItems()
        }
    }

    class RemoveAllTables(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = CommonDatabase.getInstance(context)
            db.cartDao().deleteTable()
            return true
        }
    }

    override fun onBackPressed() {
        RemoveAllTables(
            this
        ).execute()
        super.onBackPressed()
    }

    override fun onDestroy() {
        RemoveAllTables(
            this
        ).execute()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                RemoveAllTables(
                    this
                ).execute().get()
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}