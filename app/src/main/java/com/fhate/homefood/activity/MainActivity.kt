package com.fhate.homefood.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast
import com.fhate.homefood.adapter.MainAdapterRV
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.fhate.homefood.R


class MainActivity : AppCompatActivity() {

    private var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.add("Item 1")
        list.add("Item 2")
        list.add("Item 3")

        /* Формируем список типов меню:
        * Получаем ссылку на узел с ключем "food"
         * Собираем ключи всех узлов под "food" и записываем в список */
//        val foodRef = FirebaseDatabase.getInstance().reference.child("food")
//        foodRef.addListenerForSingleValueEvent(
//                object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val map = dataSnapshot.value as Map<String, Any>
//                        list = ArrayList<String>(map.keys)
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

    /* Устанавливаем RecyclerView:
     * Задаём ориентацию, определяем адаптер, обрабатываем клики */
    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MainAdapterRV(list, object : MainAdapterRV.AdapterClickListener {

                /* Обработчик клика по элементу списка */
                override fun onItemClick(position: Int) {
                    Toast.makeText(this@MainActivity, "Click on item at position " + position, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, MenuActivity::class.java)
                    intent.putExtra("menuTag", list[position])
                    startActivity(intent)
                }
            })
        }
    }
}
