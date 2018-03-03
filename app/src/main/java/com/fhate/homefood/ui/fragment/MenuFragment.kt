package com.fhate.homefood.ui.fragment

import android.content.Intent
import android.os.AsyncTask
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.toolbar.*

/* Фрагмент со список блюд выбранного меню */
/* TODO: Fix cart badger on this fragment */
class MenuFragment: Fragment() {

    private val ARGUMENT_PAGE_NAME = "arg_page_name"

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(activity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(activity) }

    private var menuList = ArrayList<MenuListItem>()
    private var menuTag = ""

    fun newInstance(name: String): MenuFragment {
        val pageFragment = MenuFragment()
        val arguments = Bundle()

        arguments.putString(ARGUMENT_PAGE_NAME, name)
        pageFragment.arguments = arguments
        return pageFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* Создание View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        (activity as AppCompatActivity).setSupportActionBar(activity.toolbar)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        val bundle = this.arguments
        if (bundle != null) {
            menuTag = bundle.getString(ARGUMENT_PAGE_NAME)
        }

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        menuList = ArrayList()
        loadData()
        pBar.visibility = View.INVISIBLE
    }

    /* Устанавливаем RecyclerView:
    * Задаём ориентацию, определяем адаптер, обрабатываем клики */
    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = MenuAdapterRV(activity, menuList, object : MenuAdapterRV.AdapterClickListener {
                override fun onButtonAddClick(position: Int) {
                    tools.addToCart(menuList[position], 1)
                    tools.setCartBadgeCount((activity as MainActivity).icon, tools.getCartCount().toString())
                    tools.makeToast(activity.resources.getString(R.string.cart_added))
                }

                override fun onButtonOverviewClick(position: Int) {
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_DISH, menuList[position].name)
                    //TODO: set OverviewActivity to OverviewActivity

                    val intent = Intent(activity, OverviewActivity::class.java)
                    intent.putExtra(repo.TAG_DISH, menuList[position].name)
                    intent.putExtra(repo.TAG_PRICE, menuList[position].price)
                    intent.putExtra(repo.TAG_DESCRIPTION, menuList[position].description)
                    intent.putExtra(repo.TAG_IMAGE_URL, menuList[position].imageUrl)
                    activity.overridePendingTransition(R.anim.left_out, R.anim.right_in)
                    activity.startActivity(intent)
                }
            })
        }
    }

    private fun loadData() {
        pBar.visibility = View.VISIBLE
        menuList = repo.getMenuList(menuTag)
        setRecyclerView()
        pBar.visibility = View.INVISIBLE
    }
}
