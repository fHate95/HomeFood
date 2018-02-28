package com.fhate.homefood.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
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
import kotlinx.android.synthetic.main.toolbar.view.*

/* Фрагмент со список блюд выбранного меню */
/* TODO: Fix cart badger on this fragment */
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
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        val bundle = this.arguments
        if (bundle != null) {
            menuTag = bundle.getString(repo.TAG_MENU)
        }

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        menuList = ArrayList()
        menuList.add(MenuItem("Печеньки", 60, false))
        menuList.add(MenuItem("Супчик", 120, true))
        menuList.add(MenuItem("Пельмешки", 80, false))

        //loadDataList()
        setRecyclerView()
    }

    /* Устанавливаем RecyclerView:
    * Задаём ориентацию, определяем адаптер, обрабатываем клики */
    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MenuAdapterRV(activity, menuList, object : MenuAdapterRV.AdapterClickListener {
                override fun onButtonAddClick(position: Int) {
                    tools.addToCart(menuList[position])
                    tools.setCartBadgeCount((activity as MainActivity).icon, tools.getCartCount().toString())
                    tools.makeToast("Item added to cart")
                }

                override fun onButtonOverviewClick(position: Int) {
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_DISH, menuList[position].name)
                    (activity as MainActivity).overviewFragment.arguments = bundle
                    activity.supportFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .setCustomAnimations(R.anim.right_in, R.anim.left_out)
                            .replace(R.id.content_frame, (activity as MainActivity).overviewFragment)
                            .commit()
                }
            })
        }
        /* title */
        activity.toolbar.tvTitle.text = menuTag
        tools.showToolbarTitle(activity.toolbar.tvTitle)
    }

    /* Формируем список меню:
        * Получаем ссылку на узел с ключем menuTag
         * Собираем ключи и значение всех узлов под menuTag и формируем из них список */
    private fun loadDataList() {
        activity.pBar.visibility = View.VISIBLE
        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD).child(menuTag)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>

                            for (entry in map.entries) {
                                //get map
                                val singleEntry = entry.value as Map<String, Any>
                                val item = MenuItem(entry.key, singleEntry["price"] as Long, false)
                                menuList.add(item)
                            }
                            setRecyclerView()
                            pBar.visibility = View.INVISIBLE
                        } catch (e: TypeCastException) {
                            tools.makeToast("Error")
                            pBar.visibility = View.INVISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast("Error")
                        pBar.visibility = View.INVISIBLE
                    }
                })
    }
}
