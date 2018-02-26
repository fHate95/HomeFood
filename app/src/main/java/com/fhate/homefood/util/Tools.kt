package com.fhate.homefood.util

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.widget.Toast
import com.fhate.homefood.R
import com.fhate.homefood.graphics.BadgeDrawable

/* Класс, выполняющий роль разлчиных инструментов */
class Tools(val context: Context) {

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
}