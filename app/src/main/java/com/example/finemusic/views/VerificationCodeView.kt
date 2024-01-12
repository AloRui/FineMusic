package com.example.finemusic.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.finemusic.R
import java.util.Random

class VerificationCodeView : ViewGroup {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val titlePaint = Paint()
    private val textPaint = Paint()
    private val linePaint = Paint()
    private val clickBackgroundPaint = Paint()
    private val clickTextPaint = Paint()

    //选择位置的键值对存储
    private val clickStepMap = mutableMapOf<Pair<Int, PointF>, Int>()
    private val lsItemPoints = mutableListOf<Pair<Int, PointF>>()

    private var w = 0
    private val h = 600
    private val random = Random()

    /**
     * 初始化画笔
     */
    init {
        titlePaint.color = resources.getColor(R.color.fontColor)
        titlePaint.textSize = 64f
        titlePaint.isAntiAlias = true

        textPaint.color = resources.getColor(R.color.fontColor)
        textPaint.textSize = 48f
        textPaint.isAntiAlias = true

        linePaint.color = resources.getColor(R.color.fontColor)
        linePaint.strokeWidth = 2f
        linePaint.isAntiAlias = true

        clickBackgroundPaint.color = resources.getColor(R.color.danger)
        clickBackgroundPaint.alpha = 110
        clickBackgroundPaint.textSize = 48f
        clickBackgroundPaint.isAntiAlias = true

        clickTextPaint.color = Color.WHITE
        clickTextPaint.textSize = 48f
        clickTextPaint.isAntiAlias = true

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        this.w = MeasureSpec.getSize(widthMeasureSpec) - 40
        w = 1200.coerceAtMost(w)
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val that = canvas!!

        that.drawText("Robot Checking", 30f, 80f, titlePaint)

        that.drawLine(30f, 110f, width.toFloat() - 30f, 110f, linePaint)

        that.drawText("Please click in order: $value", 60f, 180f, textPaint)

        for (i in 0 until lsItemPoints.size) {
            val it = lsItemPoints[i]

            that.drawText(
                it.first.toString(),
                it.second.x,
                it.second.y,
                textPaint
            )
        }

        for (i in 0 until clickStepMap.size) {
            val it = clickStepMap.toList()[i]

            that.drawCircle(
                it.first.second.x + 15f,
                it.first.second.y - 20f,
                40f,
                clickBackgroundPaint
            )

            that.drawText(
                it.second.toString(),
                it.first.second.x,
                it.first.second.y,
                clickTextPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val that = event!!

        when (that.action) {
            MotionEvent.ACTION_UP -> {

                val x = event.x
                val y = event.y

                if (lsItemPoints.any { a -> a.second.x - 50 < x && a.second.x + 50 > x && a.second.y - 50 < y && a.second.y + 50 > y }) {
                    val item =
                        lsItemPoints.first { a -> a.second.x - 50 < x && a.second.x + 50 > x && a.second.y - 50 < y && a.second.y + 50 > y }

                    if (clickStepMap.any { a -> a.key == item }) {
                        return true
                    }

                    clickStepMap[item] = clickStepMap.size + 1
                }

                invalidate()

                if (clickStepMap.size == value.length) {
                    val result = clickStepMap.map { a -> a.key.first }.joinToString("") == value
                    if (finishedListener != null) {
                        finishedListener!!.onVerificationFinished(result && value.length == 5)
                    }
                }

            }
        }

        return true
    }

    val lsNumbers = mutableListOf<Int>()

    var value = ""

    /**
     * 分配新的随机数
     */
    fun updateNumbers(
        value: String
    ) {
        lsNumbers.clear()
        clickStepMap.clear()
        lsItemPoints.clear()

        invalidate()

        this.value = value

        val numbers = value.toCharArray().toMutableList()

        for (i in numbers.indices) {
            val index = random.nextInt(numbers.size)
            val item = numbers[index]
            numbers.removeAt(index)
            lsNumbers.add(item.toString().toInt())
        }

        genItemsPoint()

        invalidate()
    }

    private fun genItemsPoint() {
        lsItemPoints.clear()

        val startPoint = PointF(80f, 300f)
        val endPoint = PointF(w.toFloat() - startPoint.x, h - startPoint.x)

        val eachWidth = (endPoint.x - startPoint.x) / 5

        for (i in 0 until lsNumbers.size) {
            val it = lsNumbers[i]
            val x = Math.max(startPoint.x, (eachWidth * (i + 1)))
            val y = Math.max(startPoint.y, random.nextInt(endPoint.y.toInt()).toFloat())

            lsItemPoints.add(Pair(it, PointF(x, y)))
        }
    }

    interface IOnVerificationFinishedListener {
        fun onVerificationFinished(result: Boolean)
    }

    private var finishedListener: IOnVerificationFinishedListener? = null

    fun setFinishedListener(listener: IOnVerificationFinishedListener) {
        this.finishedListener = listener
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        genItemsPoint()
    }

}