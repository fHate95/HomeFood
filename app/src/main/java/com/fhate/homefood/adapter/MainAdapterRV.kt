package com.fhate.homefood.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhate.homefood.R
import com.fhate.homefood.model.MainItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_food_types_item.view.*

internal class MainAdapterRV(private val items: ArrayList<MainItem>, listener: AdapterClickListener, val context: Context):
        RecyclerView.Adapter<MainAdapterRV.ViewHolder>() {

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

        fun bindItems(item: MainItem, position: Int) {
            itemView.tvName.text = item.name

            when(item.name) {
                "Супы" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_supy))
                "Горячее" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_goryachee))
                "Салаты" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_salaty))
                "Выпечка" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_vipechka))
                "Торты" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_torty))
                "Десерты" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_desert))
                "Безглютеновые блюда" -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_bezgluten))
                else -> itemView.ivBackground.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.background_texture))
            }
        }
    }
}