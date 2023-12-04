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
class SudokuGame {
    //Seçilen değiştirilebilir LiveData verisi
    var selectedCellLiveData =
        MutableLiveData<Pair<Int, Int>>() //seçilen hücre saklanmak için kullanılır
    var cellsLiveData = MutableLiveData<List<Cell>>() //Hücrelerin canlı verisini tutar.

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    //init bloğu, Sudoku oyunu oluşturulduğunda çağrılır.
    init {
        //Hücrelerin listesine ihtiyaç bulunmakta. //9*9 boyutunda bir liste
        val cells = List(9 * 9) { i -> Cell(i / 9, i % 9, i % 9) }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)

    }

    //Gelen sayının ne olduğuna karar verir, seçilen hücre alınıp güncellenir
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return

        board.getCell(selectedRow,selectedCol).value=number
        cellsLiveData.postValue(board.cells)
    }


    //Satır ve sütunları güncelleyen fonk.
    fun updateSelectedCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(
            Pair(
                row,
                col
            )
        )//satır ve sütun alınıp güncellendi, gri gönderildi.
    }
}