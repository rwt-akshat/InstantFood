package com.akrwt.instantfood.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import com.akrwt.instantfood.R
import kotlinx.android.synthetic.main.activity_ls.*

class LSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ls)
        setSupportActionBar(login_toolbar)
        setupActionBarWithNavController(Navigation.findNavController(this, R.id.fragment2))

        val sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val loggedIn = sharedPref.getBoolean("logged_in", false)
        if (loggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}