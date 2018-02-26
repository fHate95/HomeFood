package com.fhate.homefood.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhate.homefood.R
import kotlinx.android.synthetic.main.rv_food_types_item.view.*

internal class MainAdapterRV(val items: ArrayList<String>, listener: AdapterClickListener): RecyclerView.Adapter<MainAdapterRV.ViewHolder>() {

    val onClickListener: AdapterClickListener = listener

    /* Click listener for some objects in view */
    interface AdapterClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_food_types_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /* Set click listeners for view items */
        init {
            itemView.setOnClickListener {
                onClickListener.onItemClick(adapterPosition)
            }
        }

        fun bindItems(item: String, position: Int) {
            itemView.tvName.text = item
        }
    }
}