package com.akrwt.instantfood.fragments.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.akrwt.instantfood.R

class MyProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        val name:TextView = view.findViewById(R.id.profile_name)
        val phoneNumber:TextView = view.findViewById(R.id.profile_phone)
        val email:TextView = view.findViewById(R.id.profile_email_address)
        val address:TextView = view.findViewById(R.id.profile_address)

        val sharedPref = requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        name.text = sharedPref.getString("name", "User Name")
        email.text = sharedPref.getString("email","Email Address")
        phoneNumber.text = sharedPref.getString("mobile_number", "Phone Number")
        address.text = sharedPref.getString("address", "Address")

        return view
    }
}