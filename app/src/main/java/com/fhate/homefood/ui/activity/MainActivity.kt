package com.fhate.homefood.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.fhate.homefood.adapter.MainAdapterRV
import kotlinx.android.synthetic.main.activity_main.*
import com.fhate.homefood.R
import kotlinx.android.synthetic.main.toolbar.*
import com.fhate.homefood.graphics.BadgeDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.app.Fragment
import com.fhate.homefood.ui.fragment.MainFragment
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/* Главная активность со списком меню */
class MainActivity : AppCompatActivity() {

    lateinit var icon : LayerDrawable

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@MainActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.app_name)

        supportFragmentManager.beginTransaction()
                .add(R.id.content_frame, MainFragment())
                .commit()
    }
    /* Создание toolbar меню */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        icon = menu.findItem(R.id.action_cart).icon as LayerDrawable
        tools.setCartBadgeCount(icon, "0")
        return true
    }

    /* Обрабатываем клики по элементам toolbar меню */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.action_cart -> {
//                val intent = Intent(this, SettingsActivity::class.java)
//                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
