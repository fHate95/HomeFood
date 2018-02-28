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


class CartActivity: AppCompatActivity() {

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

        if (cartList.isNotEmpty())
            setRecyclerView()
        else {
            tvEmptyCart.visibility = View.VISIBLE
            shadow_view.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        hideActivity()
    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            adapter = CartAdapterRV(this@CartActivity, cartList, object : CartAdapterRV.AdapterClickListener {
                override fun onButtonIncreaseClick(position: Int) {
                    cartList[position].count++
                    repo.setCartList(cartList)
                    recyclerView.adapter.notifyDataSetChanged()
                }

                override fun onButtonDecreaseClick(position: Int) {
                    cartList[position].count--
                    if (cartList[position].count < 1) {
                        cartList.removeAt(position)
                    }
                    if (cartList.isEmpty()) {
                        setEmptyCart()
                    }

                    repo.setCartList(cartList)
                    recyclerView.adapter.notifyDataSetChanged()
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

    private fun setEmptyCart() {
        tvEmptyCart.visibility = View.VISIBLE
        shadow_view.visibility = View.INVISIBLE
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