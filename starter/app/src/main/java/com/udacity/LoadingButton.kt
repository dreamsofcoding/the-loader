package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var textColor = R.color.white
    private var bgColor = R.color.main_button_bg
    private var circleColor = R.color.main_button_circle
    private var labelTextSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        R.dimen.label_text_size.toFloat(),
        resources.displayMetrics
    )

    private var widthSize = 0
    private var heightSize = 0
    private var progress = 0f
    private var sweepAngle = 0f
    private var buttonText = context.getString(R.string.button_download)
    private var valueAnimator = ValueAnimator()
    private var overlayAlpha = 64
    private var overlayAnimator: ValueAnimator? = null

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = context.getString(R.string.button_loading)
                startAnimation()
                startOverlayAnimation()
            }

            ButtonState.Completed -> {
                buttonText = context.getString(R.string.button_download)
                stopAnimation()
            }

            ButtonState.Clicked -> {

            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = labelTextSize
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            try {

                textColor = getResourceId(R.styleable.LoadingButton_buttonTextColor, textColor)
                bgColor = getResourceId(R.styleable.LoadingButton_buttonBackgroundColor, bgColor)
                circleColor =
                    getResourceId(R.styleable.LoadingButton_progressCircleColor, circleColor)
                labelTextSize =
                    getDimension(R.styleable.LoadingButton_buttonTextSize, labelTextSize)
            } finally {
                recycle()
            }
        }

        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = ContextCompat.getColor(context, bgColor)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = ContextCompat.getColor(context, textColor)

        paint.alpha = overlayAlpha
        canvas.drawRect(0f, 0f, widthSize * progress, heightSize.toFloat(), paint)
        paint.alpha = 255

        paint.textSize = labelTextSize
        paint.textAlign = Paint.Align.CENTER

        val centerY = (heightSize / 2 - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(buttonText, (widthSize / 2).toFloat(), centerY, paint)

        val textWidth = paint.measureText(buttonText)
        val textStartX = widthSize / 2f - textWidth / 2f
        val circleStartX = textStartX + textWidth + 24f

        val radius = 20f
        val cx = circleStartX + radius
        val cy = heightSize / 2f
        val rectF = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        paint.color = ContextCompat.getColor(context, R.color.main_button_circle)
        paint.style = Paint.Style.FILL

        canvas.drawArc(rectF, -90f, sweepAngle, true, paint)
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

    fun startAnimation(duration: Long = 10000L, maxProgress: Float = 0.95f) {
        valueAnimator.cancel()
        valueAnimator = ValueAnimator.ofFloat(0f, maxProgress).apply {
            this.duration = duration
            addUpdateListener {
                progress = it.animatedValue as Float
                sweepAngle = progress * 360f
                invalidate()
            }
            start()
        }
    }

    private fun startOverlayAnimation() {
        overlayAnimator?.cancel()
        overlayAnimator = ValueAnimator.ofInt(64, 128).apply {
            duration = 800
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                overlayAlpha = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        valueAnimator.cancel()
        progress = 0f
        sweepAngle = 0f
        invalidate()
        overlayAnimator?.cancel()
        overlayAlpha = 0

    }

    fun animateToCompletion(onFinished: (() -> Unit)? = null) {
        val animator = ValueAnimator.ofFloat(progress, 1f).apply {
            duration = 500
            addUpdateListener {
                progress = it.animatedValue as Float
                sweepAngle = progress * 360f
                invalidate()
            }
            doOnEnd {
                onFinished?.invoke()
            }
        }
        animator.start()
    }

}