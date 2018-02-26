package com.fhate.homefood.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.fhate.homefood.R
import com.fhate.homefood.model.MenuItem

/* Класс для хранения данных */
class Repository(private val context: Context) {

    private val PREFS_FILENAME = "ru.fhate.pushups"
    private val REPO_CART_COUNT = "cart_count"
    private val REPO_CART_LIST = "cart_list"

    val TAG_FOOD = "food"
    val TAG_USERS = "users"
    val TAG_MENU = "menu"

    private val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

    /* Кол-во товаров в корзине */
    var cartCount: Int
        get() = prefs.getInt(REPO_CART_COUNT, 0)
        set(value) {
            prefs.edit().putInt(REPO_CART_COUNT, value).apply()
        }

    /* Получим список товаров в корзине */
    fun setCartList(list: ArrayList<MenuItem>) {
        val editor = prefs.edit()
        val json = Gson().toJson(list)

        editor.putString(REPO_CART_LIST, json)
        editor.commit()
    }

    /* Сохраним список товаров в корзине */
    fun getCartList() : ArrayList<MenuItem> {
        var list = ArrayList<MenuItem>()
        try {

            val json = prefs.getString(REPO_CART_LIST, null)

            if (json!!.isEmpty())
                return list
            else {
                val type = object : TypeToken<ArrayList<MenuItem>>() {

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