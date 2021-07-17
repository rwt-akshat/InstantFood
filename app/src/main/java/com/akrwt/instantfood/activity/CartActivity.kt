package com.akrwt.instantfood.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.database.cartdatabase.CartEntity
import com.akrwt.instantfood.database.CommonDatabase
import com.akrwt.instantfood.adapter.CartAdapter
import com.akrwt.instantfood.utils.toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var cartList: List<CartEntity>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setSupportActionBar(cart_toolbar)

        supportActionBar!!.title = "My Cart"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val recyclerView: RecyclerView = findViewById(R.id.cart_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        cartList = RetrieveCartItems(
            this
        ).execute().get()
        val adapter =
            CartAdapter(
                this,
                cartList
            )
        recyclerView.adapter = adapter

        var totalCost = 0
        for (element in cartList) {
            totalCost += element.foodCost.toInt()
        }
        place_order_btn.text = "Place Order(Total: Rs. $totalCost)"

        val userId = sharedPreferences.getString("user_id", "def")
        val resId = cartList[0].restaurant_id
        val jsonArr = JSONArray()

        for (i in cartList.indices) {
            jsonArr.put(i,JSONObject().put("food_item_id",cartList[i].food_id))
        }

        place_order_btn.setOnClickListener {
            if(checkConnectivity(this))
            sendDetails(userId, resId, totalCost, jsonArr)
            else{
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
        }
    }

    private fun sendDetails(userID: String?, resID: String?, totalCost: Int?, foods: JSONArray) {
        val jsonObj = JSONObject()
        jsonObj.put("user_id", userID)
        jsonObj.put("restaurant_id", resID)
        jsonObj.put("total_cost", totalCost.toString())
        jsonObj.put("food", foods)

       val jsonObjReq = object :
            JsonObjectRequest(Request.Method.POST, "http://13.235.250.119/v2/place_order/fetch_result/",
                jsonObj, Response.Listener {
                    val obj = it.getJSONObject("data")
                    if (obj.getBoolean("success")) {

                        val intent = Intent(this, ConfirmActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        toast("Something happened")
                    }
                }, Response.ErrorListener {
                    toast("Error: " + it.message)
                }
            ) {
            override fun getHeaders(): MutableMap<String, String> {
                val hashMap = HashMap<String, String>()
                hashMap["Content-type"] = "application/json"
                hashMap["token"] = "e3a2d90b54bcd0"
                return hashMap
            }
        }
        Volley.newRequestQueue(this).add(jsonObjReq)
    }


    class RetrieveCartItems(val context: Context) : AsyncTask<Void, Void, List<CartEntity>>() {
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val db = CommonDatabase.getInstance(context)
            return db.cartDao().getAllItems()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}