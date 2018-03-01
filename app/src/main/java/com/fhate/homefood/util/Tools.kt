package com.fhate.homefood.util

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.fhate.homefood.R
import com.fhate.homefood.graphics.BadgeDrawable
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.model.MenuItem

/* Класс, выполняющий роль разлчиных инструментов */
class Tools(val context: Context) {

    val repo = Repository(context)

    /* Определим анимацию */
    val leftInAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.left_in) }
    val leftOutAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.left_out) }
    val rightOutAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.right_out) }
    val rightInAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.right_in) }
    val fadeOutAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.fade_out) }
    val fadeInAnim: Animation
            by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(context, R.anim.fade_in) }

    /* Устанавливаем бэйдж для иконки корзины с кол-вом добавленных в неё товаров */
    fun setCartBadgeCount(icon: LayerDrawable, count: String) {
        val badge: BadgeDrawable

        //Повторно используем Drawable, если это возможно
        val badgeReuse = icon.findDrawableByLayerId(R.id.ic_badge)
        if (badgeReuse != null && badgeReuse is BadgeDrawable) {
            badge = badgeReuse
        } else {
            badge = BadgeDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyBoard(v: View, cont: Context) {
        val imm = cont.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun hideToolbarTitle(toolbar: TextView) {
        toolbar.startAnimation(leftOutAnim)
        toolbar.visibility = View.INVISIBLE
    }

    fun showToolbarTitle(toolbar: TextView) {
        toolbar.startAnimation(leftInAnim)
        toolbar.visibility = View.VISIBLE
    }

    fun getCartCount() : Int {
        var cartList = repo.getCartList()
        var count = 0

        for (i in 0 until cartList.size) {
            count += cartList[i].count
        }

        return count
    }

    fun getCartPrice() : Long {
        var cartList = repo.getCartList()
        var price: Long = 0

        for (i in 0 until cartList.size) {
            price += cartList[i].price * cartList[i].count
        }

        return price
    }

    fun addToCart(item: MenuItem) {
        var cartList = repo.getCartList()
        if (isCartContains(item, cartList)) {
            cartList[getCartItemPosition(item, cartList)].count++
        }
        else {
            cartList.add(CartItem(item.name, item.price, 1))
        }
        repo.setCartList(cartList)
    }

    private fun isCartContains(item: MenuItem, list: ArrayList<CartItem>) : Boolean {
        var res = false
        for (i in 0 until list.size) {
            if (list[i].name == item.name) {
                res = true
                break
            }
        }

        return res
    }

    private fun getCartItemPosition(item: MenuItem, list: ArrayList<CartItem>) : Int {
        var pos = 0
        for (i in 0 until list.size) {
            if (list[i].name == item.name) {
                pos = i
                break
            }
        }

        return pos
    }
}