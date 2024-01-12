package com.example.finemusic.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.example.finemusic.R
import com.example.finemusic.utils.MyApp
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener


class FlowLayout : ViewGroup {
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


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0

        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            if (lineWidth + childWidth > widthSize) {
                width = Math.max(width, lineWidth)
                height += lineHeight
                lineWidth = childWidth
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }

            if (i == childCount - 1) {
                width = Math.max(width, lineWidth)
                height += lineHeight
            }
        }

        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) widthSize else width,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else height
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        val width = r - l

        var lineWidth = 0
        var lineHeight = 0
        var curTop = 0
        var curLeft = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (lineWidth + childWidth > width) {
                curLeft = 0
                curTop += lineHeight
                lineWidth = childWidth
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }

            val left = curLeft + lp.leftMargin
            val top = curTop + lp.topMargin
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight
            child.layout(left, top, right, bottom)
            curLeft += childWidth
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private val lsItems = mutableListOf<String>()

    fun setItems(lsData: MutableList<String>) {
        this.lsItems.clear()
        this.lsItems.addAll(lsData)
        setItemViews()
    }

    private fun setItemViews() {
        this.removeAllViews()
        this.lsItems.map {
            val value = it
            val tv = TextView(MyApp.ctx).apply {
                text = it
                setTextColor(resources.getColor(R.color.fontColor))
                setBackgroundResource(R.drawable.item_search_history_bg)
                setPadding(24, 8, 24, 8)
                setOnClickListener {
                    if (itemSelectedListener != null)
                        itemSelectedListener!!.onItemSelected(value)
                }
                layoutParams = MarginLayoutParams(
                    MarginLayoutParams.WRAP_CONTENT,
                    MarginLayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 16, 16)
                }
            }
            addView(tv)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    private var itemSelectedListener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemSelected(value: String)
    }

    fun setItemSelectedListener(listener: OnItemSelectedListener) {
        this.itemSelectedListener = listener
    }
}