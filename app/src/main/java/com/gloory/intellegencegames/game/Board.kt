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

class Board(size: Int = 9, cells: List<Cell>) {
    val grid: Array<Array<Cell>> = Array(size) { row ->
        Array(size) { col ->
            val cell = cells.find { it.row == row && it.col == col } ?: Cell(row, col, 0)
            cell.isStartingCell = cell.value != 0
            cell
        }
    }

    fun getCells(): List<Cell> {
        return grid.flatten()
    }

    fun getCell(row: Int, col: Int): Cell {
        return grid[row][col]
    }

    fun setCell(row: Int, col: Int, value: Int) {
        grid[row][col].value = value
    }
}