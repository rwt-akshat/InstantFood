package com.akrwt.instantfood.fragments.loginsignup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.navArgs
import com.akrwt.instantfood.R
import com.akrwt.instantfood.activity.LSActivity
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.utils.toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class OtpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        val otp: EditText = view.findViewById(R.id.edit_otp)
        val password: EditText = view.findViewById(R.id.edit_new_pass)
        val confirmPassword: EditText = view.findViewById(R.id.edit_confirm_new_pass)
        val submitBtn: Button = view.findViewById(R.id.submitBtn)
        val args : OtpFragmentArgs by navArgs()
        val phoneNumber =args.phoneNumber

        submitBtn.setOnClickListener {
            when {
                otp.text.toString().isEmpty() -> {
                    otp.error = "This field is empty"
                    otp.requestFocus()
                }
                otp.text.toString().trim().length < 4 -> {
                    requireContext().toast("Phone number should be of length 4")
                }
                password.text.toString().isEmpty() -> {
                    password.error = "This field is empty"
                    password.requestFocus()
                }
                password.text.toString().trim().length < 4 -> {
                    requireContext().toast("Phone number should be of length atleast 4")
                }
                confirmPassword.text.toString().isEmpty() -> {
                    confirmPassword.error = "This field is empty"
                    confirmPassword.requestFocus()
                }
                password.text.toString().trim() != confirmPassword.text.toString().trim() ->{
                    requireContext().toast("Password and confirm password should be equal")
                }

                else -> {
                    if(checkConnectivity(
                            requireContext()
                        )
                    )
                    confirmChange(otp.text.toString(), password.text.toString(), phoneNumber)
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

    private fun confirmChange(otp: String, password: String, phoneNumber:String?) {
        val jsonObj = JSONObject()
        jsonObj.put("mobile_number", phoneNumber)
        jsonObj.put("password", password)
        jsonObj.put("otp", otp)


        val jsonObjReq = object :
            JsonObjectRequest(Method.POST, "http://13.235.250.119/v2/reset_password/fetch_result",
                jsonObj, Response.Listener {
                    val obj = it.getJSONObject("data")

                    if (obj.getBoolean("success")) {
                        requireContext().toast("Password changed successfully")
                        LoginFragment().loginToApp(requireContext(),phoneNumber!!, password)
                    }
                    else {
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