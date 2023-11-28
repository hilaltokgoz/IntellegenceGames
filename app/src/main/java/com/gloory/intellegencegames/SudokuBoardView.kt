package com.gloory.intellegencegames

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ       │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com                 │            
//│ ──────────────────────── │
//│ 28.11.2023                  │
//└──────────────────────────┘

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private  var sqrtSize=3
    private var size=9

    private var cellSizePixels= 0F

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels=Math.min(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(sizePixels,sizePixels)//min-max yükseklikler ayarlandı.
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)
    }
}