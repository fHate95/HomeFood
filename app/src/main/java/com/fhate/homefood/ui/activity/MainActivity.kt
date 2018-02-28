package com.fhate.homefood.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import com.fhate.homefood.R
import kotlinx.android.synthetic.main.toolbar.*
import android.graphics.drawable.LayerDrawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.fhate.homefood.ui.fragment.MainFragment
import com.fhate.homefood.ui.fragment.MenuFragment
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.toolbar.view.*
import android.support.v4.app.ActivityCompat
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build
import android.support.v4.app.ActivityOptionsCompat
import android.view.*
import android.view.animation.AccelerateInterpolator
import com.fhate.homefood.ui.fragment.OverviewFragment


/* Главная активность */
class MainActivity : AppCompatActivity() {

    lateinit var icon : LayerDrawable

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@MainActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@MainActivity) }

    val mainFragment = MainFragment()
    val menuFragment = MenuFragment()
    val overviewFragment = OverviewFragment()

    private var revealX = 0
    private var revealY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""
        toolbar.tvTitle.text = resources.getString(R.string.app_name)
        //toolbar.title = resources.getString(R.string.app_name)

        supportFragmentManager.beginTransaction()
                .add(R.id.content_frame, mainFragment)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        //content_frame.startAnimation(tools.rightInAnim)
        try {
            tools.setCartBadgeCount(icon, tools.getCartCount().toString())
        } catch (e: RuntimeException) {

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
            overviewFragment.isVisible -> {
                tools.hideToolbarTitle(toolbar.tvTitle)
                val bundle = Bundle()
                bundle.putString(repo.TAG_MENU, repo.lastMenuTag)
                menuFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.right_in, R.anim.left_out)
                        .replace(R.id.content_frame, menuFragment)
                        .commit()
            }
            else -> finish()
        }
    }

    /* Создание toolbar меню */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        icon = menu.findItem(R.id.action_cart).icon as LayerDrawable
        tools.setCartBadgeCount(icon, repo.getCartList().size.toString())
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
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* Создадим намеренность анимиованного открытия активности-корзины */
    private fun presentCartActivity(view: LinearLayout) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition")
        val revealX = (view.x + view.width).toInt()
        val revealY = (0).toInt()

        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_Y, revealY)

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }
}
