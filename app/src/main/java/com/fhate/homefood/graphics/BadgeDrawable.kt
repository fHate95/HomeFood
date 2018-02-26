package com.fhate.homefood.graphics

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import com.fhate.homefood.R

/* Класс, который отвечает за отрисовку бэйджа с кол-вом добавленных в корзину товаров */
class BadgeDrawable(context: Context): Drawable() {

    private var badgePaint: Paint
    private var badgePaint1: Paint
    private var textPaint: Paint
    private val txtRect = Rect()

    private var count = ""
    private var willDraw: Boolean = false

    private val context = context

    init {
        val mTextSize = context.resources.getDimension(R.dimen.badge_text_size)

        badgePaint = Paint()
        badgePaint.color = Color.RED
        badgePaint.isAntiAlias = true
        badgePaint.style = Paint.Style.FILL
        badgePaint1 = Paint()
        badgePaint1.color = ContextCompat.getColor(context.applicationContext, R.color.colorAccent)
        badgePaint1.isAntiAlias = true
        badgePaint1.style = Paint.Style.FILL

        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = mTextSize
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (!willDraw) {
            return
        }
        val bounds = bounds
        val width = (bounds.right - bounds.left).toFloat()
        val height = (bounds.bottom - bounds.top).toFloat()

        /* Позиционируем бэйдж в правом верхнем углу иконки */
        /* Использование Math.max в данном случае вернее, Math.min */
        val radius = Math.max(width, height) / 2 / 2
        val centerX = width - radius - 1f + 5
        val centerY = radius - 5
        if (count.length <= 2) {

            /* Отрисуем круг для бэйджа */
            canvas.drawCircle(centerX, centerY, (radius + 7.5).toInt().toFloat(), badgePaint1)
            canvas.drawCircle(centerX, centerY, (radius + 5.5).toInt().toFloat(), badgePaint)
        } else {
            canvas.drawCircle(centerX, centerY, (radius + 8.5).toInt().toFloat(), badgePaint1)
            canvas.drawCircle(centerX, centerY, (radius + 6.5).toInt().toFloat(), badgePaint)
            //canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, mBadgePaint);
        }
        /* Отрисуем счётчик внутри круга */
        textPaint.getTextBounds(count, 0, count.length, txtRect)
        val textHeight = (txtRect.bottom - txtRect.top).toFloat()
        val textY = centerY + textHeight / 2f
        if (count.length > 2)
            canvas.drawText("99+", centerX, textY, textPaint)
        else
            canvas.drawText(count, centerX, textY, textPaint)
    }

    /* Sets the count (i.e notifications) to display. */
    fun setCount(count: String) {
        this.count = count

        /* Отрисовываем бэйдж только если счётчик > 0 */
        willDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(cf: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}