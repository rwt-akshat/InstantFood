package com.akrwt.instantfood.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.adapter.FaqAdapter
import com.akrwt.instantfood.utils.FAQs

class FAQsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_f_a_qs, container, false)
        val recyclerview: RecyclerView = v.findViewById(R.id.faq_recyclerView)
        recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerview.layoutManager = layoutManager
        recyclerview.addItemDecoration(
            DividerItemDecoration(
                recyclerview.context,
                layoutManager.orientation
            )
        )


        val queAnsList = ArrayList<FAQs>()

        queAnsList.add(
            FAQs(
                "What Instant Food is doing for us?",
                "At instant food we strive to add value to your business. We understand the challenges you face on a daily basis in getting the food from the best restaurants. We are here to take care of selecting all the best restaurants near you. "
            )
        )


        queAnsList.add(
            FAQs(
                "Can I place order on call?",
                "Sorry, we don,t accept order on call. However you can us on +91-1234567890 for any help related to placing order."
            )
        )
        queAnsList.add(
            FAQs(
                "How do I know my order is confirmed?",
                "We'll send you an order confirmation email once we received your order."
            )
        )
        queAnsList.add(
            FAQs(
                "How do I edit my order?",
                "Please call us on +91-1234567890 or write email to us abcd@gmail.com mentioning your order ID."
            )
        )
        queAnsList.add(
            FAQs(
                "Is delivery free?",
                "Yes. There are no delivery or handling charges."
            )
        )
        queAnsList.add(
            FAQs(
                "Famous restaurants in my area?",
                "There aren't any"
            )
        )
        queAnsList.add(
            FAQs(
                "When is the order delivered?",
                "Our suppliers deliver the products within 2-3 hours of orders being placed."
            )
        )
        queAnsList.add(
            FAQs(
                "Why my location is not serviceable?",
                "We are currently live only in few cities. We will be expanding soon."
            )
        )
        queAnsList.add(
            FAQs(
                "I received wrong products, what should i do?",
                "Please see our return and cancellation policy www.abc.com ."
            )
        )
        queAnsList.add(
            FAQs(
                "Can I pay offline?",
                "Sorry we do not allow cash on delivery yet."
            )
        )
        queAnsList.add(
            FAQs(
                "Can I pay online?",
                "Yes can do payment through debit cards, UPI, NEFT."
            )
        )


        val adapter = FaqAdapter(
            requireContext(),
            queAnsList
        )
        recyclerview.adapter = adapter

        return v
    }
}