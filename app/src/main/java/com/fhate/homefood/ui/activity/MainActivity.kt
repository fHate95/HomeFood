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
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import com.fhate.homefood.model.MenuListItem
import android.view.*
import android.view.MenuItem
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat
import android.graphics.drawable.GradientDrawable.Orientation
import android.graphics.drawable.GradientDrawable
import com.fhate.homefood.ui.fragment.MainFragment
import kotlinx.android.synthetic.main.toolbar.view.*
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import com.fhate.homefood.model.MainItem
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


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

    private var list = ArrayList<MainItem>()

    val mainFragment = MainFragment()
    val menuFragment = MenuFragment()

    private lateinit var database: FirebaseDatabase
    private lateinit var valueReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""

        database = FirebaseDatabase.getInstance()
        valueReference = database.getReference(repo.TAG_VALUES)

        val gradientDrawable = GradientDrawable(
                Orientation.TL_BR,
                intArrayOf(ContextCompat.getColor(this, R.color.colorGreenGradient1),
                        ContextCompat.getColor(this, R.color.colorGreenGradient2)))

        toolbar.background = gradientDrawable

        if (tools.isOnline()) {
            loadDataList()
        }
        else {
            tools.showAlertDialog(resources.getString(R.string.alert_connection_error),
                    resources.getString(R.string.close_app), null)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            tools.setCartBadgeCount(icon, tools.getCartCount().toString())
        } catch (e: RuntimeException) {

        }

        if (menuFragment.isVisible) {
            menuFragment.setRecyclerView()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        when {
            menuFragment.isVisible -> {
                tools.hideToolbarTitle(toolbar.tvTitle)
                supportFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.left_in, R.anim.right_out)
                        .replace(R.id.content_frame, mainFragment)
                        .commit()
            }
            else -> finish()
        }
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
                //presentCartActivity(rootLayout)
                val intent = Intent(this, CartActivity::class.java)
                //overridePendingTransition(R.anim.right_out, R.anim.left_in)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* STORAGE STUFF */

    /* Загрузим все элементы меню */
    private fun loadDataList() {
//        activity.pBar.visibility = View.VISIBLE
        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD)
        foodRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as Map<String, Any>
                            var item = ArrayList<String>(map.keys)

                            for (i in 0 until item.size) {
                                list.add(MainItem(item[i], ""))
                            }

                            list = tools.sortMainList(list)
                            repo.setTypeList(list)

                            for (i in 0 until list.size) {
                                loadMenuDataList(i)
                            }
                        } catch (e: TypeCastException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                        catch (E: NullPointerException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast(resources.getString(R.string.error))
                    }
                })
    }

    private fun loadMenuDataList(pos: Int) {
        var menuList = ArrayList<MenuListItem>()

        val foodRef = FirebaseDatabase.getInstance().reference.child(repo.TAG_FOOD).child(list[pos].name)
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

                            list = tools.sortMainList(list)
                            repo.setMenuList(menuList, list[pos].name)
                            /* Если всё загружено */
                            if (pos >= list.size - 1) {
                                pBar.visibility = View.INVISIBLE

                                supportFragmentManager.beginTransaction()
                                        .add(R.id.content_frame, mainFragment)
                                        .commit()
                            }

                        } catch (e: TypeCastException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                        catch (E: NullPointerException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast(resources.getString(R.string.error))
                    }
                })
    }
}