package com.fhate.homefood.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fhate.homefood.adapter.MainAdapterRV
import kotlinx.android.synthetic.main.fragment_main.*
import com.fhate.homefood.R
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import android.support.v7.app.AppCompatActivity
import com.fhate.homefood.model.MainItem
import com.fhate.homefood.model.MenuListItem
import com.fhate.homefood.ui.activity.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

/* Фрагмент со список доступных для выбора меню */
class MainFragment: Fragment() {

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(activity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(activity) }

    private var list = ArrayList<MainItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* Создание View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        (activity as AppCompatActivity).setSupportActionBar(activity.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        list = ArrayList()
        list = repo.getTypeList()
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
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_MENU, list[position].name)
                    repo.lastMenuTag = list[position].name
                    (activity as MainActivity).menuFragment.arguments = bundle
                    activity.supportFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .setCustomAnimations(R.anim.right_in, R.anim.left_out)
                            .replace(R.id.content_frame, (activity as MainActivity).menuFragment)
                            .commit()
                }
            }, context)
            /* title */
            activity.toolbar.tvTitle.text = resources.getString(R.string.app_name)
            tools.showToolbarTitle(activity.toolbar.tvTitle)
        }
    }
}