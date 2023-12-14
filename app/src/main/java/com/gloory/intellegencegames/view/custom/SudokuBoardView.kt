package com.gloory.intellegencegames.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.gloory.intellegencegames.game.Cell
import com.gloory.intellegencegames.game.SudokuGame
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
    private val conflictCellPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    //Görünüm ne kadar büyük olacak. Kare tahtayı korumak için
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)//min-max yükseklikler ayarlandı.
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)
        fillCells(canvas)
        drawLine(canvas)
        drawText(canvas)
    }

    private fun updateMeasurements(width: Int) {
        cellSizePixels = (width / size).toFloat() //hücreler bölündü.
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F

    }

    private fun fillCells(canvas: Canvas) {//tuval alınıp hücrelerin doldurulacağı alan
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

            if (value == 0) {
                //draw notes
                cell.notes.forEach { note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString=note.toString()
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                        (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textHeight / 2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()

                val paintToUse =
                    if (cell.isStartingCell) startingCellTextPaint else textPaint//başlangıç mı değil mi kontrol edildi.

                paintToUse.getTextBounds(
                    valueString,
                    0,
                    valueString.length,
                    textBounds
                )//text boyamak için sınırları verilir
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                //metni hücre ortasına ortalamak için
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

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate() //geçersiz kılkar,hücreler yeniden oluşturulur.
    }


    //verileri kaçmadığından emin olmak için
    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int) {
        }
    }

    fun checkConflictsAndDraw(canvas: Canvas) {

        cells?.let { (row, col) ->
            val selectedNumber = board[row][col]

            // Çelişki kontrolü için seçilen hücrenin satır ve sütununu kontrol et
            for (i in 0 until 9) {
                // Aynı satırdaki çelişki kontrolü
                if (i != selectedCol && selectedNumber ==board [row][i]) {
                    drawConflictCell(canvas, selectedRow, i)
                }

                // Aynı sütundaki çelişki kontrolü
                if (i != selectedRow && selectedNumber == board[i][selectedCol]) {
                    drawConflictCell(canvas, i, selectedCol)
                }
            }

            // 3x3'lük bölge içinde çelişki kontrolü
            val startRow = selectedRow / 9 * 9
            val startCol = selectedCol / 9 * 9
            for (i in startRow until startRow + 9) {
                for (j in startCol until startCol + 9) {
                    if (!(i == selectedRow && j == selectedCol) && selectedNumber == board[i][j]) {
                        drawConflictCell(canvas, i, j)
                    }
                }
            }
        }
    }

    private fun drawConflictCell(canvas: Canvas, row: Int, col: Int) {
        // Çelişki olan hücreyi belirtilen renkte boyar
        val left = col * cellSizePixels
        val top = row * cellSizePixels
        canvas.drawRect(left, top, left + cellSizePixels, top + cellSizePixels, conflictCellPaint)
    }


}