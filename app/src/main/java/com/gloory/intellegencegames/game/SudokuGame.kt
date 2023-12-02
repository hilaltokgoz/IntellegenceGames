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
    private var selectedRow = -1
    private var selectedCol = -1

    //init bloğu, Sudoku oyunu oluşturulduğunda çağrılır.
    init {
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    //Satır ve sütunları güncelleyen fonk.
    fun updateSelectedCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row,col))//satır ve sütun alınıp güncellendi, gri gönderildi.
    }
}