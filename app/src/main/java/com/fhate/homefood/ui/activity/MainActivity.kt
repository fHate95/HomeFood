package com.fhate.homefood.ui.activity

import android.app.Dialog
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
import android.widget.ScrollView
import com.fhate.homefood.model.MainItem
import com.fhate.homefood.ui.fragment.ReviewFragment
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.info_dialog_view.view.*


/* Главная активность */
class MainActivity : AppCompatActivity() {

    lateinit var icon : LayerDrawable //иконка корзины

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@MainActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@MainActivity) }

    private var list = ArrayList<MainItem>()

    val mainFragment = MainFragment()
    val menuFragment = MenuFragment()
    val reviewFragment = ReviewFragment()

    /* объекты db */
    private lateinit var database: FirebaseDatabase
    private lateinit var valueReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        /* Устанавливаем главуню тему приложения до onCreate метода
         * Это позволяет прописать отдельную тему для экрана загрузки (SplashScreen)
          * Splash тема для активности установлена в манифест файле */
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = ""

        /* Объявляем FireBaseDataBase */
        database = FirebaseDatabase.getInstance()
        valueReference = database.getReference(repo.TAG_VALUES)

        val gradientDrawable = GradientDrawable(
                Orientation.TL_BR,
                intArrayOf(ContextCompat.getColor(this, R.color.colorGreenGradient1),
                        ContextCompat.getColor(this, R.color.colorGreenGradient2)))

        toolbar.background = gradientDrawable

        /* Если подключение присутствует - загрузим данные из базы
         * в противном случае уведомим пользователя об ошибке соединения */
        if (tools.isOnline()) {
            loadDataList()
        }
        else {
            tools.showAlertDialog(resources.getString(R.string.alert_connection_error),
                    resources.getString(R.string.close_app), null)
        }
    }

    /* onResume вызывается каждый раз, когда активность становится видимой,
     * поэтому обновляем счётчик бэйджа корзины здесь.
      * Кол-во элементов в корзине полуаем через tools класс из репозитория */
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

    /* Обработчик систмной кнопки "назад"
     * Если мы во фрагменте меню, то возвращаемся в главный фрагмент,
      * если мы в информации, то также возвращаемся в главный,
      * если мы уже в главном приложении - убиваем активность и закрываем приложение */
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
            reviewFragment.isVisible -> {
                supportFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
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

        if (!mainFragment.isVisible) {
            menu.findItem(R.id.action_about).isVisible = false
        }

        return true
    }

    /* Обрабатываем клики по элементам toolbar меню */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            /* Корзина */
            R.id.action_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                //overridePendingTransition(R.anim.right_out, R.anim.left_in)
                startActivity(intent)
                true
            }
            /* "О нас" */
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            /* back */
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

    /* Загрузим все данные меню до отображения view
     * Чтобы не загружать их при переходах в разделы меню
      * Единественное, что будет загружаться при заполнении списков меню - изображения (в адаптере) */
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
                                /* Показыаем фрагмент */
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

    /* Вызываем фрагмент "о нас" */
    private fun showAboutDialog() {
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content_frame, reviewFragment)
                .commit()
    }
}