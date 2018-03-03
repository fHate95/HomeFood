package com.fhate.homefood.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fhate.homefood.R
import kotlinx.android.synthetic.main.toolbar.*
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.support.design.widget.TabLayout
import com.fhate.homefood.ui.fragment.MenuFragment
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import com.fhate.homefood.model.MenuListItem
import android.view.*
import android.view.MenuItem
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*


/* Главная активность */
class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: FragmentPagerAdapter

    lateinit var icon : LayerDrawable

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@MainActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@MainActivity) }

    private var revealX = 0
    private var revealY = 0

    private var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""

        if (tools.isOnline()) {
            loadDataList()
            //Async().execute()
        }
        else {
            tools.showAlertDialog(resources.getString(R.string.alert_connection_error),
                    resources.getString(R.string.close_app), null)
        }

//        pagerAdapter = FragmentPagerAdapter(supportFragmentManager)
//        pager.adapter = pagerAdapter
//        tabs.setupWithViewPager(pager)
    }

    override fun onResume() {
        super.onResume()
        try {
            tools.setCartBadgeCount(icon, tools.getCartCount().toString())
        } catch (e: RuntimeException) {

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private inner class FragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return MenuFragment().newInstance(list[position])
        }

        override fun getItemPosition(`object`: Any?): Int {
            pager.adapter.notifyDataSetChanged()
            return super.getItemPosition(`object`)
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    private fun loadDataList() {
        pBar.visibility = View.VISIBLE
        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>
                            list = ArrayList<String>(map.keys)

                            if (list.size > 4) {
                                tabs.tabMode = TabLayout.MODE_SCROLLABLE
                            } else {
                                tabs.tabMode = TabLayout.MODE_FIXED
                            }

                            for (i in 0 until list.size) {
                                loadMenuDataList(list[i])
                            }

                            //pBar.visibility = View.INVISIBLE
                            pagerAdapter = FragmentPagerAdapter(supportFragmentManager)
                            pager.adapter = pagerAdapter
                            tabs.setupWithViewPager(pager)
                            pager.currentItem = list.size - 1

                            for (i in 0 until tabs.tabCount) {
                                tabs.getTabAt(i)!!.text = list[i]
                            }

                            //pBar.visibility = View.INVISIBLE
                            //ContentView.visibility = View.VISIBLE

                        } catch (e: TypeCastException) {
                            tools.makeToast("Error")
                            pBar.visibility = View.INVISIBLE
                        }
                        catch (E: NullPointerException) {
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

    /* Формируем список меню:
    * Получаем ссылку на узел с ключем menuTag
     * Собираем ключи и значение всех узлов под menuTag и формируем из них список */
    private fun loadMenuDataList(tag: String) {
        var menuList = ArrayList<MenuListItem>()

        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD).child(tag)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>

                            for (entry in map.entries) {
                                val singleEntry = entry.value as Map<String, Any>
                                val item = MenuListItem(entry.key, singleEntry["price"] as Long,
                                        singleEntry["description"] as String, singleEntry["image"] as String)
                                menuList.add(item)
                            }

                            repo.setMenuList(menuList, tag)

                            /* Some trick to fix  */
                            for (i in 0 until list.size) {
                                pager.currentItem = i
                            }
                            pager.currentItem = 0
                            pBar.visibility = View.INVISIBLE
                            ContentView.visibility = View.VISIBLE
                        } catch (e: TypeCastException) {
                            tools.makeToast("Error")
                        }
                        catch (E: NullPointerException) {
                            tools.makeToast("Error")
                            pBar.visibility = View.INVISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast("Error")
                    }
                })
    }

    /* Создание toolbar меню */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        icon = menu.findItem(R.id.action_cart).icon as LayerDrawable
        tools.setCartBadgeCount(icon, tools.getCartCount().toString())
        return true
    }

    /* Обрабатываем клики по элементам toolbar меню */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.action_cart -> {
                presentCartActivity(rootLayout)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* Создадим намеренность анимиованного открытия активности-корзины */
    private fun presentCartActivity(view: FrameLayout) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition")
        val revealX = (view.x + view.width).toInt()
        val revealY = (0).toInt()

        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_Y, revealY)

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    internal inner class Async : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg urls: String?): Boolean {

            loadDataList()

            return true
            }

        override fun onPreExecute() {
            pBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(res: Boolean) {
            pBar.visibility = View.INVISIBLE
            ContentView.visibility = View.VISIBLE
        }
    }
}