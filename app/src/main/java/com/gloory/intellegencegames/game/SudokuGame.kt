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

    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>() //mevcut hücrenin notlarını tutar.

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false //not alıp almadığını tutacak

    private val board: Board

    //init bloğu, Sudoku oyunu oluşturulduğunda çağrılır.
    init {
        //Hücrelerin listesine ihtiyaç bulunmakta. //9*9 boyutunda bir liste
        val cells = List(9 * 9) { i -> Cell(i / 9, i % 9, i % 9) }
        cells[0].notes = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
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
}