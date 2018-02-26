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
import com.fhate.homefood.ui.activity.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainFragment: Fragment() {

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(activity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(activity) }

    private var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* Создание View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        (activity as AppCompatActivity).setSupportActionBar(activity.toolbar)
        //activity.toolbar.title = resources.getString(R.string.app_name)

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        list = ArrayList()
        list.add("Item 1")
        list.add("Item 2")
        list.add("Item 3")

        //loadDataList()
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
                    activity.toolbar.title = ""
                    val bundle = Bundle()
                    bundle.putString(repo.TAG_MENU, list[position])
                    //val fragment = MenuFragment()
                    (activity as MainActivity).menuFragment.arguments = bundle
                    activity.supportFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .setCustomAnimations(R.anim.right_in, R.anim.left_out)
                            .replace(R.id.content_frame, (activity as MainActivity).menuFragment)
                            .commit()
                }
            })
            activity.toolbar.title = resources.getString(R.string.app_name)
        }
    }

    /* Формируем список типов меню:
        * Получаем ссылку на узел с ключем "food"
         * Собираем ключи всех узлов под "food" и записываем в список */
    private fun loadDataList() {
        activity.pBar.visibility = View.VISIBLE
        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>
                            list = ArrayList<String>(map.keys)
                            setRecyclerView()
                            activity.pBar.visibility = View.INVISIBLE
                        } catch (e: TypeCastException) {
                            tools.makeToast("Error")
                            activity.pBar.visibility = View.INVISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast("Error")
                        activity.pBar.visibility = View.INVISIBLE
                    }
                })
    }
}