package com.fhate.homefood.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fhate.homefood.R
import com.fhate.homefood.adapter.MenuAdapterRV
import com.fhate.homefood.model.MenuItem
import com.fhate.homefood.ui.activity.MainActivity
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.toolbar.*

class MenuFragment: Fragment() {

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(activity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(activity) }

    private var menuList = ArrayList<MenuItem>()
    private var menuTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* Создание View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        (activity as AppCompatActivity).setSupportActionBar(activity.toolbar)

        val bundle = this.arguments
        if (bundle != null) {
            menuTag = bundle.getString(repo.TAG_MENU)
            activity.toolbar.title = menuTag
        }

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        menuList.add(MenuItem("Item 1", 0))
        menuList.add(MenuItem("Item 2", 0))
        menuList.add(MenuItem("Item 3", 0))

        //loadDataList()
        setRecyclerView()
    }

    /* Устанавливаем RecyclerView:
    * Задаём ориентацию, определяем адаптер, обрабатываем клики */
    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MenuAdapterRV(activity, menuList, object : MenuAdapterRV.AdapterClickListener {
//                override fun onButtonClick(position: Int) {
//                    Toast.makeText(this@MenuActivity, "Click on button in " + list[position], Toast.LENGTH_SHORT).show()
//                }

                override fun onItemClick(position: Int) {
                    //tools.makeToast("Click on item at position " + position)
                    //repo.cartCount++
                    //tools.setCartBadgeCount((activity as MainActivity).icon, repo.cartCount.toString())
                }
            })
        }
    }

    /* Формируем список меню:
        * Получаем ссылку на узел с ключем menuTag
         * Собираем ключи и значение всех узлов под menuTag и формируем из них список */
    private fun loadDataList() {
        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD).child(menuTag)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>

                            for (entry in map.entries) {
                                //get map
                                val singleEntry = entry.value as Map<String, Any>
                                val item = MenuItem(entry.key, singleEntry["price"] as Long)
                                menuList.add(item)
                            }

                            setRecyclerView()
                        } catch (e: TypeCastException) {
                            tools.makeToast("Error")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
    }
}
