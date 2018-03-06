package com.fhate.homefood.ui.fragment

import android.content.Intent
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
import com.fhate.homefood.model.MenuListItem
import com.fhate.homefood.ui.activity.MainActivity
import com.fhate.homefood.ui.activity.OverviewActivity
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
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

    private var menuList = ArrayList<MenuListItem>()
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
        loadData()
    }

    /* Устанавливаем RecyclerView:
    * Задаём ориентацию, определяем адаптер, обрабатываем клики */
    fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MenuAdapterRV(activity, menuList, object : MenuAdapterRV.AdapterClickListener {
                override fun onButtonAddClick(position: Int) {
                    tools.addToCart(menuList[position], 1)
                    tools.setCartBadgeCount((activity as MainActivity).icon, tools.getCartCount().toString())
                    tools.makeToast(activity.resources.getString(R.string.cart_added))
                    adapter.notifyDataSetChanged()
                }

                override fun onItemClick(position: Int) {
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_DISH, menuList[position].name)
                    val intent = Intent(activity, OverviewActivity::class.java)
                    intent.putExtra(repo.TAG_DISH, menuList[position].name)
                    intent.putExtra(repo.TAG_PRICE, menuList[position].price)
                    intent.putExtra(repo.TAG_DESCRIPTION, menuList[position].description)
                    intent.putExtra(repo.TAG_IMAGE_URL, menuList[position].imageUrl)
                    //activity.overridePendingTransition(R.anim.left_out, R.anim.right_in)
                    activity.startActivity(intent)
                }

                override fun onButtonOverviewClick(position: Int) {
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_DISH, menuList[position].name)
                    val intent = Intent(activity, OverviewActivity::class.java)
                    intent.putExtra(repo.TAG_DISH, menuList[position].name)
                    intent.putExtra(repo.TAG_PRICE, menuList[position].price)
                    intent.putExtra(repo.TAG_DESCRIPTION, menuList[position].description)
                    intent.putExtra(repo.TAG_IMAGE_URL, menuList[position].imageUrl)
                    //activity.overridePendingTransition(R.anim.left_out, R.anim.right_in)
                    activity.startActivity(intent)
                }
            })
        }
        activity.toolbar.tvTitle.text = menuTag
        tools.showToolbarTitle(activity.toolbar.tvTitle)
    }

    private fun loadData() {
        menuList = repo.getMenuList(menuTag)
        setRecyclerView()
    }
}
