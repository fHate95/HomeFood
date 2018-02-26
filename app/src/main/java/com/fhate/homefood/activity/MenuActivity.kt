package com.fhate.homefood.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast
import com.fhate.homefood.R
import com.fhate.homefood.adapter.MenuAdapterRV
import com.fhate.homefood.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.toolbar.*


class MenuActivity: AppCompatActivity() {

    private var menuList = ArrayList<MenuItem>()
    private var tag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        tag = intent.getStringExtra("menuTag")
        toolbar.title = tag

        menuList.add(MenuItem("Item 1", 0))
        menuList.add(MenuItem("Item 2", 0))
        menuList.add(MenuItem("Item 3", 0))

//        val foodRef = FirebaseDatabase.getInstance().reference.child("food").child(tag)
//        foodRef.addListenerForSingleValueEvent(
//                object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val map = dataSnapshot.value as Map<String, Any>
//
//                        for (entry in map.entries) {
//
//                            //Get user map
//                            val singleEntry = entry.value as Map<String, Any>
//                            //Get phone field and append to list
//                            //phoneNumbers.add(singleUser["phone"] as Long)
//                            val item = MenuItem(entry.key, singleEntry["price"] as Long)
//                            menuList.add(item)
//                        }
//
//                        setRecyclerView()
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//
//                    }
//                })

        setRecyclerView()
    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MenuAdapterRV(this@MenuActivity, menuList, object : MenuAdapterRV.AdapterClickListener {
//                override fun onButtonClick(position: Int) {
//                    Toast.makeText(this@MenuActivity, "Click on button in " + list[position], Toast.LENGTH_SHORT).show()
//                }

                override fun onItemClick(position: Int) {
                    Toast.makeText(this@MenuActivity, "Click on item at position " + position, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}