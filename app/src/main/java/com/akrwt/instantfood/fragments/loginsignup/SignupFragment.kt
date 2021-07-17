package com.akrwt.instantfood.fragments.loginsignup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.akrwt.instantfood.activity.MainActivity
import com.akrwt.instantfood.R
import com.akrwt.instantfood.activity.LSActivity
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.utils.toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SignupFragment : Fragment() {
    private lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        sharedPref = requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        val userName: EditText = view.findViewById(R.id.edit_userName)
        val email: EditText = view.findViewById(R.id.edit_email_address)
        val phoneNumber: EditText = view.findViewById(R.id.signup_edit_phoneNumber)
        val password: EditText = view.findViewById(R.id.signup_edit_password)
        val confirmPassword: EditText = view.findViewById(R.id.edit_confirm_password)
        val address: EditText = view.findViewById(R.id.edit_location)
        val signUpBtn: Button = view.findViewById(R.id.signupBtn)

        signUpBtn.setOnClickListener {
            when {
                userName.text.toString().isEmpty() -> {
                    userName.error = "This field is empty"
                    userName.requestFocus()
                }
                userName.text.trim().length < 4 -> {
                    requireContext().toast("User name should be greater than 3")
                }
                email.text.toString().isEmpty() -> {
                    email.error = "This field is empty"
                    email.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches() -> {
                    requireContext().toast("Enter a valid email!")
                }
                phoneNumber.text.toString().trim().isEmpty() -> {
                    phoneNumber.error = "This field is empty"
                    phoneNumber.requestFocus()
                }
                phoneNumber.text.toString().trim().length != 10 -> {
                    requireContext().toast("Phone Number should be of length 10")
                }
                password.text.toString().trim().isEmpty() -> {
                    password.error = "This field is empty"
                    password.requestFocus()
                }
                password.text.toString().trim().length < 5 -> {
                    requireContext().toast("Password should be more than 4")
                }
                confirmPassword.text.toString().trim().isEmpty() -> {
                    confirmPassword.error = "This field is empty"
                    confirmPassword.requestFocus()
                }
                address.text.toString().trim().isEmpty() -> {
                    address.error = "This field is empty"
                    address.requestFocus()
                }
                password.text.toString().trim() != confirmPassword.text.toString() -> {
                    requireContext().toast("Confirm password doesn't match with password")
                }
                else -> {
                    if(checkConnectivity(
                            requireContext()
                        )
                    )
                    signUpToApp(
                        userName.text.toString(),
                        phoneNumber.text.toString(),
                        password.text.toString(),
                        address.text.toString(),
                        email.text.toString()
                    )
                    else{
                        AlertDialog.Builder(context).apply {
                            setTitle("Error")
                                .setMessage("Internet Connection is not found")
                            setPositiveButton("Open Settings") { _, _ ->
                                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                                LSActivity().finish()
                            }
                            setNegativeButton("Exit") { _, _ ->
                                LSActivity().finishAffinity()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }

        return view
    }

    private fun signUpToApp(
        name: String,
        mobile: String,
        password: String,
        address: String,
        email: String
    ) {
        val jsonObj = JSONObject()
        jsonObj.put("name", name)
        jsonObj.put("mobile_number", mobile)
        jsonObj.put("password", password)
        jsonObj.put("address", address)
        jsonObj.put("email", email)

        val jsonObjReq = object :
            JsonObjectRequest(Method.POST, "http://13.235.250.119/v2/register/fetch_result",
                jsonObj, Response.Listener {
                    val obj = it.getJSONObject("data")
                    if (obj.getBoolean("success")) {
                        val dataObj = obj.getJSONObject("data")
                        val sharedEdit = sharedPref.edit()
                        sharedEdit.putBoolean("logged_in", true)
                        sharedEdit.putString("user_id", dataObj.getString("user_id"))
                        sharedEdit.putString("name", dataObj.getString("name"))
                        sharedEdit.putString("email", dataObj.getString("email"))
                        sharedEdit.putString("mobile_number", dataObj.getString("mobile_number"))
                        sharedEdit.putString("address", dataObj.getString("address"))
                        sharedEdit.apply()

                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        requireContext().toast(obj.getString("errorMessage"))
                    }
                }, Response.ErrorListener {
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