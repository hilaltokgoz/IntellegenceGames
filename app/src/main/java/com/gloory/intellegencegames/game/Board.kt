package com.gloory.intellegencegames.game
// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 4.12.2023                │
//└──────────────────────────┘
//Hücre listesini ve board boyutunu tutar.

class Board(val size: Int, val cells: List<Cell>) {

    fun getCell(row: Int, col: Int) =
        cells[row * size + col]//ilk satır önde gidecek, ikinci satır bir sonraki satırda olacak....Tek boyutlu dizi

}