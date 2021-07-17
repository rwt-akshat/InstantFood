package com.akrwt.instantfood.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun checkConnectivity(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < 23) {
        val activeNetwork = connectivityManager.activeNetworkInfo
        return if (activeNetwork?.isConnected != null) activeNetwork.isConnected else false

    } else {

        val n = connectivityManager.activeNetwork
        if (n != null) {
            val nc = connectivityManager.getNetworkCapabilities(n)!!
            return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ))
        }
        return false
    }
}
