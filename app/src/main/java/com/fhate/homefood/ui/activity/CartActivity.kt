package com.fhate.homefood.ui.activity

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.fhate.homefood.R
import com.fhate.homefood.adapter.CartAdapterRV
import com.fhate.homefood.adapter.MenuAdapterRV
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.ViewAnimationUtils
import android.animation.Animator
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Handler
import android.support.annotation.Dimension
import android.support.v4.content.ContextCompat
import android.transition.TransitionManager
import android.widget.Toast
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail


class CartActivity: AppCompatActivity() {

    private var MAIL_USERNAME = "homefood.kirov@gmail.com"
    private var MAIL_PASSWORD = "6t24paaYq"
    private var MAIL_TO = "f.hate95@yandex.ru"
    private var MAIL_SUBJECT = "Заказ №28"
    private var MAIL_BODY = ""

    private var revealX = 0
    private var revealY = 0

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@CartActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@CartActivity) }

    private var cartList = ArrayList<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""
        toolbar.tvTitle.text = resources.getString(R.string.cart)

        animateActivity(savedInstanceState)

//        cartList.add(CartItem("Item 1"))
//        cartList.add(CartItem("Item 2"))
//        cartList.add(CartItem("Item 3"))

        cartList = repo.getCartList()
        updateInfo()

        if (cartList.isNotEmpty())
            setRecyclerView()
        else {
            tvEmptyCart.visibility = View.VISIBLE
        }

        buttonGoToOrder.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            overridePendingTransition(R.anim.left_out, R.anim.right_in)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        hideActivity()
    }

    private fun updateInfo() {
        when (tools.getCartCount() % 10) {
            1 -> tvCount.text = resources.getString(R.string.total) + " " + tools.getCartCount().toString() + " " +
                    resources.getString(R.string.info_dish_count_1)
            2,3,4 -> tvCount.text = resources.getString(R.string.total) + " " + tools.getCartCount().toString() + " " +
                    resources.getString(R.string.info_dish_count_2_4)
            else -> tvCount.text = resources.getString(R.string.total) + " " + tools.getCartCount().toString() + " " +
                    resources.getString(R.string.info_dish_count_5_0)
        }
        tvPrice.text = tools.getCartPrice().toString() + resources.getString(R.string.ruble_sign)

    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = CartAdapterRV(this@CartActivity, cartList, object : CartAdapterRV.AdapterClickListener {
                override fun onButtonIncreaseClick(position: Int) {
                    cartList[position].count++
                    repo.setCartList(cartList)
                    recyclerView.adapter.notifyDataSetChanged()
                    updateInfo()
                }

                override fun onButtonDecreaseClick(position: Int) {
                    cartList[position].count--
                    if (cartList[position].count < 1) {
                        cartList.removeAt(position)
                    }
                    if (cartList.isEmpty()) {
                        tvEmptyCart.visibility = View.VISIBLE
                    }

                    repo.setCartList(cartList)
                    recyclerView.adapter.notifyDataSetChanged()
                    updateInfo()
                }

            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun animateActivity(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(repo.EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(repo.EXTRA_CIRCULAR_REVEAL_Y)) {
            /* TODO */
            rootLayout.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(repo.EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(repo.EXTRA_CIRCULAR_REVEAL_Y, 0)


            val viewTreeObserver = rootLayout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        showActivity(revealX, revealY)
                        rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            rootLayout.visibility = View.VISIBLE
        }
    }

    private fun showActivity(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(rootLayout.width, rootLayout.height) * 1.1f)
            val circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0f, finalRadius)

            circularReveal.duration = 400
            circularReveal.interpolator = AccelerateInterpolator()

            rootLayout.visibility = View.VISIBLE
            circularReveal.start()
        } else {
            finish()
        }
    }

    private fun hideActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish()
        } else {
            val finalRadius = (Math.max(rootLayout.width, rootLayout.height) * 1.1f)
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0f)

            circularReveal.duration = 400
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rootLayout.visibility = View.INVISIBLE
                    finish()
                }
            })
            circularReveal.start()
        }
    }
}