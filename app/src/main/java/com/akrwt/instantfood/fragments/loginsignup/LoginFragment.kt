package com.akrwt.instantfood.fragments.loginsignup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akrwt.instantfood.activity.MainActivity
import com.akrwt.instantfood.R
import com.akrwt.instantfood.activity.LSActivity
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.utils.toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.json.JSONObject


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.sign_up_text.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        view.forgot_text.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        val etPhone:EditText = view.findViewById(R.id.edit_phoneNumber)
        val etPassword:EditText = view.findViewById(R.id.edit_password)

        view.loginBtn.setOnClickListener {

            when {
                etPhone.text.toString().trim().isEmpty() -> {
                    view.edit_phoneNumber.error = ("This field is empty")
                    view.edit_phoneNumber.requestFocus()
                }
                etPhone.text.toString().trim().length != 10 -> {
                    requireContext().toast("Phone Number should be of length 10")
                }
                etPassword.text.toString().trim().isEmpty() -> {
                    view.edit_password.error = ("This field is empty")
                    view.edit_password.requestFocus()
                }
                etPassword.text.toString().trim().length < 5 -> {
                    requireContext().toast("Password should be length more than 4")
                }
                else -> {
                    if(checkConnectivity(
                            requireContext()
                        )
                    )
                    loginToApp(requireContext(),etPhone.text.toString(), etPassword.text.toString())
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

    fun loginToApp(context:Context,phone: String, password: String) {
        val jsonObj = JSONObject()
        jsonObj.put("mobile_number", phone)
        jsonObj.put("password", password)

        val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)


        val jsonObjReq = object :
            JsonObjectRequest(Request.Method.POST, "http://13.235.250.119/v2/login/fetch_result/",
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
                        context.startActivity(intent)
                    } else {
                        requireContext().toast("Account does not exist. Sign up first")
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