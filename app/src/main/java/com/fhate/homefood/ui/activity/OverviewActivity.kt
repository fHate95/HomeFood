package com.fhate.homefood.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ScrollView
import com.fhate.homefood.R
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.model.MenuListItem
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*


class OverviewActivity : AppCompatActivity() {

    lateinit var icon : LayerDrawable

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@OverviewActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@OverviewActivity) }

    private var dishTag = "no description"
    private var dishPrice: Long = 0
    private var dishDescription = ""
    private var dishImageUrl = ""

    var cartList = ArrayList<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        if (intent != null) {
            try {
                dishTag = intent.getStringExtra(repo.TAG_DISH)
                dishPrice = intent.getLongExtra(repo.TAG_PRICE, 0)
                dishDescription = intent.getStringExtra(repo.TAG_DESCRIPTION)
                dishImageUrl = intent.getStringExtra(repo.TAG_IMAGE_URL)
            } catch (e: IllegalStateException) {

            }
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""
        toolbar.tvTitle.text = dishTag
        tvPrice.text = dishPrice.toString() + resources.getString(R.string.ruble_sign)
        tvDescription.text = dishDescription

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(ContextCompat.getColor(this, R.color.colorGreenGradient1),
                        ContextCompat.getColor(this, R.color.colorGreenGradient2)))

        toolbar.background = gradientDrawable

        Picasso.with(this)
                .load(dishImageUrl)
                .placeholder(R.drawable.ico_error_loading)
                .error(R.drawable.ico_error_loading)
                .into(ivImage)

        cartList = repo.getCartList()


        buttonIncrease.setOnClickListener {
            var value = tvCount.text.toString().toInt()
            var price = tvPrice.text.toString().substring(0, tvPrice.text.length - 1).toLong()
            tvCount.text = (value + 1).toString()
            tvPrice.text = (price + dishPrice).toString() + resources.getString(R.string.ruble_sign)
        }

        buttonDecrease.setOnClickListener {
            var value = tvCount.text.toString().toInt()
            var price = tvPrice.text.toString().substring(0, tvPrice.text.length - 1).toLong()
            if (value > 1) {
                tvCount.text = (value - 1).toString()
                tvPrice.text = (price - dishPrice).toString() + resources.getString(R.string.ruble_sign)
            }
        }

        buttonAddToCart.setOnClickListener {
            buttonAddToCart.startAnimation(tools.clickAnim)
            tools.addToCart(MenuListItem(dishTag, dishPrice, dishDescription, dishImageUrl), tvCount.text.toString().toInt())
            tools.setCartBadgeCount(icon, tools.getCartCount().toString())
            tools.makeToast(resources.getString(R.string.cart_added))
        }
    }

    /* Создание toolbar меню */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        icon = menu.findItem(R.id.action_cart).icon as LayerDrawable
        tools.setCartBadgeCount(icon, tools.getCartCount().toString())

        menu.findItem(R.id.action_about).isVisible = false

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
    private fun presentCartActivity(view: ScrollView) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition")
        val revealX = (view.x + view.width).toInt()
        val revealY = (0).toInt()

        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(repo.EXTRA_CIRCULAR_REVEAL_Y, revealY)

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

}