package com.fhate.homefood.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.fhate.homefood.R
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.model.MenuItem

/* Класс для хранения данных */
class Repository(private val context: Context) {

    private val PREFS_FILENAME = "ru.fhate.pushups"
    private val REPO_CART_COUNT = "cart_count"
    private val REPO_CART_LIST = "cart_list"
    private val REPO_LAST_MENU = "last_menu"

    val TAG_FOOD = "food"
    val TAG_USERS = "users"
    val TAG_MENU = "menu"
    val TAG_DISH = "dish"
    val TAG_NAME = "name"
    val TAG_VALUES = "values"
    val TAG_ORDER_NUMBER = "order_number"
    val TAG_ADDRESS = "address"
    val TAG_ORDER_COUNT = "order_count"
    val TAG_TOTAL_PRICE = "total_price"
    val TAG_MAIL_TO = "mail_to"
    val TAG_MAIL_USERNAME = "mail_username"
    val TAG_MAIL_PASSWORD = "mail_password"

    val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
    val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    val TAG_AUTOCOMPLETE_NAME = "autocomplete_name"
    val TAG_AUTOCOMPLETE_ADDRESS = "autocomplete_address"

    private val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

    /* Кол-во товаров в корзине */
    var cartCount: Int
        get() = prefs.getInt(REPO_CART_COUNT, 0)
        set(value) {
            prefs.edit().putInt(REPO_CART_COUNT, value).apply()
        }

    /* Тэг последнего посещенного раздела меню
    * Используется для возврата в меню из OverviewFragment'а*/
    var lastMenuTag: String
    get() = prefs.getString(REPO_LAST_MENU, "")
    set(value) {
        prefs.edit().putString(REPO_LAST_MENU, value).apply()
    }

    /* Получим список товаров в корзине */
    fun setCartList(list: ArrayList<CartItem>) {
        val editor = prefs.edit()
        val json = Gson().toJson(list)

        editor.putString(REPO_CART_LIST, json)
        editor.commit()
    }

    /* Сохраним список товаров в корзине */
    fun getCartList() : ArrayList<CartItem> {
        var list = ArrayList<CartItem>()
        try {

            val json = prefs.getString(REPO_CART_LIST, null)

            if (json!!.isEmpty())
                return list
            else {
                val type = object : TypeToken<ArrayList<CartItem>>() {

                }.type
                list = Gson().fromJson(json, type)
            }
            return list
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return list
    }

    fun setAutoCompleteList(list: ArrayList<String>, tag: String) {
        val editor = prefs.edit()
        val json = Gson().toJson(list)

        editor.putString(tag, json)
        editor.commit()
    }

    fun getAutoCompleteList(tag: String) : ArrayList<String> {
        var list = ArrayList<String>()
        try {

            val json = prefs.getString(tag, null)

            if (json!!.isEmpty())
                return list
            else {
                val type = object : TypeToken<ArrayList<String>>() {

                }.type
                list = Gson().fromJson(json, type)
            }
            return list
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return list
    }
}