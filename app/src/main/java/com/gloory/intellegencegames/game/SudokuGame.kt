package com.gloory.intellegencegames.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 2.12.2023                │
//└──────────────────────────┘

//Tahtanın durumu burada saklanır.
//LiveData'lar burada tutulacak
//LiveData'ya view-viewModel arasındaki bağlantıyı sağlar.
//Güncelleme geldiğinde view bunu görür,kendi alanlarının günceller

open class SudokuGame {
    //Seçilen değiştirilebilir LiveData verisi
    private val _selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val selectedCellLiveData: LiveData<Pair<Int, Int>> = _selectedCellLiveData

    private val _cellsLiveData = MutableLiveData<List<Cell>>()
    val cellsLiveData: LiveData<List<Cell>> = _cellsLiveData

    private var selectedRow = -1
    private var selectedCol = -1

    private lateinit var board: Board
    init {
        setDifficulty(SudokuDifficulty.EASY)
     /*   _selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        _cellsLiveData.postValue(board.getCells())*/

    }
    fun isGameCompleted(): Boolean {
        return board.grid.all { row ->
            row.all { cell ->
                cell.isStartingCell || (cell.value != 0 && isFillValid(board.grid, cell.row, cell.col, cell.value))
            }
        }
    }


    //Gelen sayının ne olduğuna karar verir, seçilen hücre alınıp güncellenir
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        board.setCell(selectedRow, selectedCol, number)
        _cellsLiveData.postValue(board.getCells())
    }

    //Satır ve sütunları güncelleyen fonk.
    fun updateSelectedCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        _selectedCellLiveData.postValue(Pair(row, col))
    }
    fun delete() {
        if (selectedRow == -1 || selectedCol == -1) return

        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        board.setCell(selectedRow, selectedCol, 0)
        _cellsLiveData.postValue(board.getCells())
    }

    //sayıları rastgele board>'ta sıralatmak için
    fun generateSudoku(): Array<Array<Cell>> {
        val sudoku = Array(9) { Array(9) { Cell(0, 0, 0) } }
        fillSudoku(sudoku)
        return sudoku
    }

    fun fillSudoku(sudoku: Array<Array<Cell>>, row: Int = 0, col: Int = 0): Boolean {
        if (row == 9) {
            return true
        }

        val nextRow = if (col == 8) row + 1 else row
        val nextCol = (col + 1) % 9

        val shuffledNumbers = (1..9).shuffled()

        for (num in shuffledNumbers) {
            if (isFillValid(sudoku, row, col, num)) {
                sudoku[row][col].value = num
                if (fillSudoku(sudoku, nextRow, nextCol)) {
                    return true
                }
                sudoku[row][col].value = 0
            }
        }
        return false
    }

    fun isFillValid(sudoku: Array<Array<Cell>>, row: Int, col: Int, num: Int): Boolean {
        return sudoku[row].none { it.value == num } &&
                sudoku.none { it[col].value == num } &&
                sudoku.sliceArray((row / 3) * 3 until (row / 3 + 1) * 3)
                    .all { it.sliceArray((col / 3) * 3 until (col / 3 + 1) * 3).none { it.value == num } }
    }

    fun setDifficulty(difficulty: SudokuDifficulty) {
        val cellsToRemove = when (difficulty) {
            SudokuDifficulty.EASY -> 2
            SudokuDifficulty.MEDIUM -> 50
            SudokuDifficulty.HARD -> 53
        }
       val generatedArray= emptyRandomCells(cellsToRemove)

        generatedArray.forEach { row -> println(row.joinToString(" ")) }

        //Hücrelerin listesine ihtiyaç bulunmakta. //9*9 boyutunda bir liste
        val cells = List(9 * 9) { i ->
            val row = i / 9
            val col = i % 9
            val cellValue = generatedArray[row][col].value
            val isStartingCell = cellValue != 0
            Cell(row, col, cellValue, isStartingCell)
        }
       // cells[0].notes = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        board = Board(9, cells)
        selectedRow = -1
        selectedCol = -1
        _selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        _cellsLiveData.postValue(board.getCells())
    }

    // Belirli sayıda hücreyi rastgele boşaltan fonksiyon
    private fun emptyRandomCells(cellsToEmpty: Int): Array<Array<Cell>> {

        val fullBoard= generateSudoku()
        val random = java.util.Random()

        repeat(cellsToEmpty) {
            var row: Int
            var col: Int

            // Rastgele bir konum seç ve boşalt
            do {
                row = random.nextInt(9)
                col = random.nextInt(9)
            } while (fullBoard[row][col].value == 0) // Eğer hücre zaten boşsa tekrar seç

            fullBoard[row][col].value = 0
        }
        return fullBoard
    }
}

enum class SudokuDifficulty {
    EASY,
    MEDIUM,
    HARD
}

