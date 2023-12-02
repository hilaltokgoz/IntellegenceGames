package com.gloory.intellegencegames.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
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
    //boyut şimdilik sadece 3*3 olacak.
    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixels = 0F

    //Başlangıçta seçili satır ya da sütun yok o yüzden negatif ayarlandı.
    private var selectedRow = 0
    private var selectedCol = 0

    private var listener: SudokuBoardView.OnTouchListener? = null

    //Kalın çizgiler hücre gruplarını tanımlar
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    //İncew çizgiler diğer tüm çizgilerdir
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    //Hücreleri boyamak için kullanılacak
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }


    //Görünüm ne kadar büyük olacak. Kare tahtayı korumak için
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)//min-max yükseklikler ayarlandı.
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / size).toFloat() //hücreler bölündü.
        fillCells(canvas)
        drawLine(canvas)
    }

    private fun fillCells(canvas: Canvas) {//tuval alınıp hücrelerin doldurulacağı alan
        if (selectedRow == -1 || selectedCol == -1) return // hücre seçilmediyse bir şey yapma
        for (r in 0..size) {
            for (c in 0..size) {
                if (r == selectedRow && c == selectedCol) { //Hücre seçiliyse
                    fillCell(canvas, r, c, selectedCellPaint)
                } else if (r == selectedRow || c == selectedCol) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
            }
        }
    }

    //Hücre başlangıcından diğer hücre başlangıcına kadar dikdörtgen çizer.
    private fun fillCell(
        canvas: Canvas,
        r: Int,
        c: Int,
        paint: Paint
    ) {//Tuvale nokta çizimi yapılır.
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    private fun drawLine(canvas: Canvas) { //Tuval alacak, çizgileri belirlemek için kullanılır
        canvas.drawRect(
            0F,
            0F,
            width.toFloat(),
            height.toFloat(),
            thickLinePaint
        )// dış sınır çizilir

        for (i in 1 until size) { // ne zaman kalın çizgiye karşılık ince çizgi çizilecek
            val painToUse =
                when (i % sqrtSize) {//Karekök(sqrt) modu 0 olduğunda kalın çizgi çizecektir
                    0 -> thickLinePaint
                    else -> thinLinePaint
                }
            canvas.drawLine(//ilk x çizilir, sonra y
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                painToUse
            )
            canvas.drawLine( //yatay çizgi için
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                painToUse
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { // dokunma olayını tutar, boolean tutar
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {  //Kişi ekrana dokunursa
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }

    }

    private fun handleTouchEvent(x: Float, y: Float) { //seçilenleri satır ve sütuna çevirmeliyiz
        val possibleSelectedRow = (y / cellSizePixels).toInt() //satır ve sütun tam sayıdır
        val possibleSelectedCol = (x / cellSizePixels).toInt() //satır ve sütun tam sayıdır
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()// kulllanıcı arayüzündeki her şey yeniden çizilir.
    }

    fun registerListener(listener: SudokuBoardView.OnTouchListener) {
        this.listener = listener
    }


    //verileri kaçmadığından emin olmak için
    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int) {
        }
    }

}