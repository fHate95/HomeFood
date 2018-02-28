package com.fhate.homefood.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.R
import kotlinx.android.synthetic.main.rv_cart_item.view.*

internal class CartAdapterRV(val context: Context, private val items: ArrayList<CartItem>, listener: AdapterClickListener):
        RecyclerView.Adapter<CartAdapterRV.ViewHolder>() {

    val onClickListener: AdapterClickListener = listener

    interface AdapterClickListener {
        fun onButtonIncreaseClick(position: Int)
        fun onButtonDecreaseClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_cart_item, parent, false)
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
            itemView.buttonIncrease.setOnClickListener {
                onClickListener.onButtonIncreaseClick(adapterPosition)
            }
            itemView.buttonDecrease.setOnClickListener {
                onClickListener.onButtonDecreaseClick(adapterPosition)
            }
        }

        fun bindItems(item: CartItem, position: Int) {
            //TODO: set image
            itemView.tvName.text = item.name
            itemView.tvCount.text = item.count.toString()
            itemView.tvPrice.text = (item.price * item.count).toString() + context.resources.getString(R.string.ruble_sign)
        }
    }
}