package com.fhate.homefood.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhate.homefood.R
import com.fhate.homefood.model.MenuItem
import kotlinx.android.synthetic.main.rv_menu_item.view.*

internal class MenuAdapterRV(context: Context, val items: ArrayList<MenuItem>, listener: AdapterClickListener): RecyclerView.Adapter<MenuAdapterRV.ViewHolder>() {

    val context = context
    val onClickListener: AdapterClickListener = listener

    /* Click listener for some objects in view */
    interface AdapterClickListener {
        //fun onButtonClick(position: Int)
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_menu_item, parent, false)
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
//            itemView.buttonTest.setOnClickListener {
//                onClickListener.onButtonClick(adapterPosition)
//            }

            itemView.setOnClickListener {
                onClickListener.onItemClick(adapterPosition)
            }
        }

        fun bindItems(item: MenuItem, position: Int) {
            itemView.tvName.text = item.name
            itemView.tvPrice.text = item.price.toString() + context.resources.getString(R.string.ruble_sign)
        }
    }
}