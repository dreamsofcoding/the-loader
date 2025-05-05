package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = "We are Loading"
                startAnimation()
            }
            ButtonState.Completed -> {
                buttonText = "Download"
                stopAnimation()
            }

            ButtonState.Clicked -> {

            }
        }
    }

    private var buttonText = "Download"
    private var progress = 0f
    private var animatedWidth = 0f
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        isClickable = true
        valueAnimator.duration = 2000
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = resources.getColor(R.color.colorPrimary, null)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = resources.getColor(R.color.colorPrimaryDark, null)
        canvas.drawRect(0f, 0f, progress, heightSize.toFloat(), paint)

        paint.color = resources.getColor(R.color.white, null)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 40f

        val centerY = (heightSize / 2 - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(buttonText, (widthSize / 2).toFloat(), centerY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startAnimation() {
        valueAnimator.setFloatValues(0f, widthSize.toFloat())
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.start()
    }

    private fun stopAnimation() {
        valueAnimator.cancel()
        progress = 0f
        invalidate()
    }
}