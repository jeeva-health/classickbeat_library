package ai.heart.classickbeats.ui.fragment.common

import ai.heart.classickbeats.R
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep
import androidx.core.content.ContextCompat

class CircleProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mStrokeWidth = 4f
    private var progress = 0f
    private var min = 0
    private var max = 100

    /**
     * Start the progress at 12 o'clock
     */
    private val startAngle = -90f
    private var mForegroundColor = ContextCompat.getColor(context, R.color.bright_blue_2)
    private var mBackgroundColor = ContextCompat.getColor(context, R.color.very_soft_blue)
    private var rectF: RectF = RectF()
    private var backgroundPaint: Paint
    private var foregroundPaint: Paint
    private var textPaint: Paint

    fun getProgress(): Float {
        return progress
    }

    @Keep
    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
        requestLayout()
    }

    fun getMin(): Int {
        return min
    }

    fun setMin(min: Int) {
        this.min = min
        invalidate()
        requestLayout()
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
        requestLayout()
    }

    fun setColor(foregroundColor: Int, backgroundColor: Int) {
        mForegroundColor = foregroundColor
        mBackgroundColor = backgroundColor
        backgroundPaint.color = mBackgroundColor
        foregroundPaint.color = mForegroundColor
        invalidate()
        requestLayout()
    }

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleProgressBar,
            defStyleAttr, 0
        )
        //Reading values from the XML layout
        try {
            mStrokeWidth = typedArray.getDimension(
                R.styleable.CircleProgressBar_progressBarThickness,
                mStrokeWidth
            )
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress)
            mBackgroundColor = typedArray.getInt(
                R.styleable.CircleProgressBar_progressbarColorBackground,
                mBackgroundColor
            )
            mForegroundColor =
                typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, mForegroundColor)
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min)
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max)
        } finally {
            typedArray.recycle()
        }

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mBackgroundColor
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mForegroundColor
            style = Paint.Style.STROKE
            strokeWidth = mStrokeWidth
        }

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.white)
            textSize = 18.0f * 2.25f //todo:: the value should be dpi of device
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF, backgroundPaint)
        val angle = 360 * progress / max
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint)
//        val textStr = ((100 - progress) * SCAN_DURATION / 100).toInt().toString()
//        val xPos = rectF.centerX()
//        val yPos = rectF.centerY() + textPaint.textSize / 4
//        canvas.drawText(textStr, xPos, yPos, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        rectF[0 + mStrokeWidth / 2.toFloat(), 0 + mStrokeWidth / 2.toFloat(), min - mStrokeWidth / 2.toFloat()] =
            min - mStrokeWidth / 2.toFloat()
    }

    fun setProgressWithAnimation(progress: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
        objectAnimator.duration = 1500
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }
}