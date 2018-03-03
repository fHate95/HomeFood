package com.fhate.homefood.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fhate.homefood.R
import com.fhate.homefood.model.MenuListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_menu_item.view.*

internal class MenuAdapterRV(val context: Context, private val items: ArrayList<MenuListItem>, listener: AdapterClickListener):
        RecyclerView.Adapter<MenuAdapterRV.ViewHolder>() {

    val onClickListener: AdapterClickListener = listener

    /* Click listener for some objects in view */
    interface AdapterClickListener {
        fun onButtonAddClick(position: Int)
        fun onButtonOverviewClick(position: Int)
        //fun onItemClick(position: Int)
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
            itemView.buttonAdd.setOnClickListener {
                onClickListener.onButtonAddClick(adapterPosition)
            }

            itemView.buttonOverview.setOnClickListener {
                onClickListener.onButtonOverviewClick(adapterPosition)
            }

//            itemView.setOnClickListener {
//                onClickListener.onItemClick(adapterPosition)
//            }
        }

        fun bindItems(item: MenuListItem, position: Int) {
            //TODO: set image
            itemView.tvName.text = item.name
            itemView.tvPrice.text = item.price.toString() + context.resources.getString(R.string.ruble_sign)

            Picasso.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ico_error_loading)
                    .resize(200, 150)
                    .error(R.drawable.ico_error_loading)
                    .into(itemView.ivImage)

            itemView.pBar.visibility = View.INVISIBLE

            //itemView.ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ico_test_pizza))
        }
    }
}