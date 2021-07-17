package com.akrwt.instantfood.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.akrwt.instantfood.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        toolbar.overflowIcon = resources.getDrawable(R.drawable.ic_sort)
        val navController = Navigation.findNavController(this,
            R.id.fragment
        )
        setupActionBarWithNavController(navController, drawer_layout)
        navigation_view.setupWithNavController(navController)

        val sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "def")
        val phoneNumber = sharedPref.getString("mobile_number", "def")

        val headerView = navigation_view.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.header_name).text = name
        headerView.findViewById<TextView>(R.id.header_phone).text = phoneNumber

        navigation_view.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Do you really want to logout from Innerwork")
                .setPositiveButton("Yes") { _, _ ->
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this, LSActivity::class.java))
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
            return@setOnMenuItemClickListener true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this,
            R.id.fragment
        )
        return NavigationUI.navigateUp(navController, drawer_layout)
    }
}