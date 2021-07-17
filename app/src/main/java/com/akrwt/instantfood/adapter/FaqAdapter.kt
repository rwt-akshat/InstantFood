package com.akrwt.instantfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrwt.instantfood.R
import com.akrwt.instantfood.utils.FAQs

class FaqAdapter(val context: Context, private val faqList: ArrayList<FAQs>) :
    RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.text_question)
        val answer: TextView = itemView.findViewById(R.id.text_answers)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.faq_item_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = faqList[position]
        holder.question.text = "Q.${position+1} ${current.question}"
        holder.answer.text = "A.${position+1} ${current.answer}"
    }
}