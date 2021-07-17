package com.akrwt.instantfood.fragments.loginsignup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import com.akrwt.instantfood.R
import com.akrwt.instantfood.activity.LSActivity
import com.akrwt.instantfood.utils.checkConnectivity
import com.akrwt.instantfood.utils.toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        val phoneNumber: EditText = view.findViewById(R.id.forgot_phoneNumber)
        val emailAddress: EditText = view.findViewById(R.id.forgot_email_address)
        val nxtBtn: Button = view.findViewById(R.id.nextBtn)

        nxtBtn.setOnClickListener {
            when {
                phoneNumber.text.toString().isEmpty() -> {
                    phoneNumber.error = "This field is empty"
                    phoneNumber.requestFocus()
                }
                emailAddress.text.toString().isEmpty() -> {
                    emailAddress.error = "This field is empty"
                    emailAddress.requestFocus()
                }
                phoneNumber.text.toString().trim().length != 10 -> {
                    requireContext().toast("Phone number should be of length 10")
                }
                !Patterns.EMAIL_ADDRESS.matcher(emailAddress.text.toString().trim()).matches() -> {
                    requireContext().toast("Enter a valid email!")
                }
                else -> {
                    if(checkConnectivity(
                            requireContext()
                        )
                    )
                        sendEmailAndPhone(phoneNumber.text.toString(), emailAddress.text.toString())
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

    private fun sendEmailAndPhone(phoneNumber: String, emailAddress: String) {
        val jsonObj = JSONObject()
        jsonObj.put("mobile_number", phoneNumber)
        jsonObj.put("email", emailAddress)

        val jsonObjReq = object :
            JsonObjectRequest(Method.POST, "http://13.235.250.119/v2/forgot_password/fetch_result",
                jsonObj, Response.Listener {
                    val obj = it.getJSONObject("data")

                    if (obj.getBoolean("success") && obj.getBoolean("first_try")) {
                        val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtpFragment(phoneNumber)
                        Navigation.findNavController(requireView()).navigate(action)

                    } else if (obj.getBoolean("success") && !obj.getBoolean("first_try")) {
                        val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtpFragment(phoneNumber)
                        Navigation.findNavController(requireView()).navigate(action)
                        requireContext().toast("Use same otp sent to your email address")
                    }
                    else {
                        requireContext().toast("Some error occurred!")
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