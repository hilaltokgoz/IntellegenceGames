package com.gloory.intellegencegames.game

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
    var selectedCellLiveData =
        MutableLiveData<Pair<Int, Int>>() //seçilen hücre saklanmak için kullanılır
    var cellsLiveData = MutableLiveData<List<Cell>>() //Hücrelerin canlı verisini tutar.

    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>() //mevcut hücrenin notlarını tutar.

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false //not alıp almadığını tutacak

    private lateinit var board: Board
    //init bloğu, Sudoku oyunu oluşturulduğunda çağrılır.
    init {
        setDifficulty(SudokuDifficulty.EASY)
    }
    //Gelen sayının ne olduğuna karar verir, seçilen hücre alınıp güncellenir
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {//mevcut hücre sayı içerir.
                cell.notes.remove(number) //sayıyı kaldır
            } else {
                cell.notes.add(number)//sayıyı ekle
            }
            highlightedKeysLiveData.postValue(cell.notes)//ui da tekrar güncellenecek
        } else { //not alınmayan kısımlar
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

    //Satır ve sütunları güncelleyen fonk.
    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(
                Pair(
                    row,
                    col
                )
            ) //satır ve sütun alınıp güncellendi, gri gönderildi.
            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)//mevcut hücrenin değeri yayınlanır
            }
        }

        println(" Hilal Test " + cell.notes.toString())
    }

    //Not alma durumunu değiştiren fonk.
    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf<Int>() //not varsa mevcut notu, yoksa boş not seti alınır.
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
        val cell = board.getCell(selectedRow, selectedCol)
        if (isTakingNotes) {
            cell.notes.clear()  //not alınıyorsa hepsini sil
            highlightedKeysLiveData.postValue(setOf())  //tuşlar boş tutulur.
        } else {
            cell.value = 0 //hücre değiştiği için değer 0'a eşitlendi.
        }
        cellsLiveData.postValue(board.cells)  //yeni hücreler arayüzüne gönderiliyor.
    }

    //sayıları rastgele board>'ta sıralatmak için
      fun generateSudoku(): Array<Array<Int>> {
        val sudoku = Array(9) { Array(9) { 0 } }
        fillSudoku(sudoku)
        return sudoku
    }

    fun fillSudoku(sudoku: Array<Array<Int>>, row: Int = 0, col: Int = 0): Boolean {
        if (row == 9) {
            return true
        }

        val nextRow = if (col == 8) row + 1 else row
        val nextCol = (col + 1) % 9

        val shuffledNumbers = (1..9).shuffled()

        for (num in shuffledNumbers) {
            if (isFiilValid(sudoku, row, col, num)) {
                sudoku[row][col] = num
                if (fillSudoku(sudoku, nextRow, nextCol)) {
                    return true
                }
                sudoku[row][col] = 0
            }
        }
        return false
    }

    fun isFiilValid(sudoku: Array<Array<Int>>, row: Int, col: Int, num: Int): Boolean {
        return sudoku[row].none { it == num } &&
                sudoku.none { it[col] == num } &&
                sudoku.sliceArray((row / 3) * 3 until (row / 3 + 1) * 3)
                    .all { it.sliceArray((col / 3) * 3 until (col / 3 + 1) * 3).none { it == num } }
    }

    fun setDifficulty(difficulty: SudokuDifficulty) {
        val cellsToRemove = when (difficulty) {
            SudokuDifficulty.EASY -> 43
            SudokuDifficulty.MEDIUM -> 50
            SudokuDifficulty.HARD -> 53
        }
       val generatedArray= emptyRandomCells(cellsToRemove)

        generatedArray.forEach { row -> println(row.joinToString(" ")) }

        //Hücrelerin listesine ihtiyaç bulunmakta. //9*9 boyutunda bir liste
        val cells = List(9 * 9) { i ->
            Cell(
                i / 9, i % 9, generatedArray[i / 9][i % 9]
            )
        }
        cells[0].notes = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    // Belirli sayıda hücreyi rastgele boşaltan fonksiyon
    private fun emptyRandomCells(cellsToEmpty: Int): Array<Array<Int>> {

        val fullBoard= generateSudoku()
        val random = java.util.Random()

        repeat(cellsToEmpty) {
            var row: Int
            var col: Int

            // Rastgele bir konum seç ve boşalt
            do {
                row = random.nextInt(9)
                col = random.nextInt(9)
            } while (fullBoard[row][col] == 0) // Eğer hücre zaten boşsa tekrar seç

            fullBoard[row][col] = 0
        }
        return fullBoard
    }
}

enum class SudokuDifficulty {
    EASY,
    MEDIUM,
    HARD
}

