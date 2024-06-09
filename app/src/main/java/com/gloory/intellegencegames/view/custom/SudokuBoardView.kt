package com.gloory.intellegencegames.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.gloory.intellegencegames.game.Cell
import kotlin.math.min

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
    private var noteSizePixels = 0F

    var canvas: Canvas? = null

    var conflictedList = mutableListOf<Cell>()

    //Başlangıçta seçili satır ya da sütun yok o yüzden negatif ayarlandı.
    private var selectedRow = 0
    private var selectedCol = 0

    private var listener: SudokuBoardView.OnTouchListener? = null

    private var cells: List<Cell>? = null

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

    //text için boya rengi ayarlanır
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK

    }
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
    }

    //diğer hücrelerle olan bağlantı
    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#acacac")
    }

    //textSize ayarlaması
    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }
    private val redPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#FF3325")
    }

    //Görünüm ne kadar büyük olacak. Kare tahtayı korumak için
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)//min-max yükseklikler ayarlandı.
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateMeasurements(width)
        fillCells(canvas)
        drawLine(canvas)
        drawText(canvas)
        // this.canvas = canvas
    }

    private fun updateMeasurements(width: Int) {
        cellSizePixels = (width / size).toFloat() //hücreler bölündü.
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F

    }

    private fun fillCells(canvas: Canvas) {
        if (selectedRow == -1 || selectedCol == -1) return // hücre seçilmediyse bir şey yapma

        cells?.forEach {
            val r = it.row
            val c = it.col
            if (it.isStartingCell) {
                fillCell(canvas, r, c, startingCellPaint)
            } else if (r == selectedRow && c == selectedCol) { //Hücre seçiliyse
                fillCell(canvas, r, c, selectedCellPaint)
            } else if (r == selectedRow || c == selectedCol) {
                fillCell(canvas, r, c, conflictingCellPaint)
            } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }
        }
        conflictedList.forEach {
            val r = it.row
            val c = it.col
            fillCell(canvas, r, c, redPaint)
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

    //Burada gerçek sayılar ekrana çizilecek
    private fun drawText(canvas: Canvas) {
        cells?.forEach { cell ->
            val value = cell.value
            val textBounds = Rect()
            if (value != 0) {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint

                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString,
                    (col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                    (row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2,
                    paintToUse
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { // dokunma olayını tutar, boolean tutar
        return if (event.action == MotionEvent.ACTION_DOWN) {
            handleTouchEvent(event.x, event.y)
            true
        } else {
            false
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
        checkConflictsAndDraw()
        invalidate()// kulllanıcı arayüzündeki her şey yeniden çizilir.
    }

    fun registerListener(listener: SudokuBoardView.OnTouchListener) {
        this.listener = listener
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate() //geçersiz kılkar,hücreler yeniden oluşturulur.
    }

    //verileri kaçmadığından emin olmak için
    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
        fun onGameCompleted(isCompleted: Boolean)

    }

    fun checkConflictsAndDraw() {
        conflictedList.clear()
        var hasConflicts = false
        cells?.forEachIndexed { index1, cell ->
            val selectedRow = cell.row
            val selectedCol = cell.col
            cells?.forEachIndexed { index2, cell2 ->
                val c = cell2.col
                val r = cell2.row
                if (cell.value == cell2.value && cell.value != 0 && index1 != index2) {
                    if (r == selectedRow || c == selectedCol ||
                        (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize)
                    ) {
                        hasConflicts = true
                        conflictedList.add(cell)
                    }
                }
            }
        }
        if (!hasConflicts && isGameCompleted()) {
            listener?.onGameCompleted(true)
        } else {
            listener?.onGameCompleted(false)
        }
        invalidate()
    }
    fun isGameCompleted(): Boolean {
        var  isCompleted = cells != null
        cells?.forEach { cell ->
            if (cell.value == 0) {
                isCompleted = false
                return@forEach
            }
        }
        return isCompleted
    }

      /*  conflictedList.clear()
        canvas?.let { canvas ->
            cells?.forEachIndexed { index1, cell ->
                val selectedRow = cell.row
                val selectedCol = cell.col
                cells?.forEachIndexed { index2, cell2 ->
                    val c = cell2.col
                    val r = cell2.row
                    if (cell.value == cell2.value && cell.value != 0 && index1 != index2) {
                        if (r == selectedRow || c == selectedCol) {
                            Log.i(
                                "hilal",
                                "Confilicted cell col:${cell.col} - row:${cell.row} - value:${cell.value} (ROW-COL)"
                            )
                            conflictedList.add(cell)
                        } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                            Log.i(
                                "hilal",
                                "Confilicted cell col:${cell.col} - row:${cell.row} - value:${cell.value} (SQRT)"
                            )
                            conflictedList.add(cell)
                        }
                    }
                }
            }
        }
        invalidate()
    }*/
}
