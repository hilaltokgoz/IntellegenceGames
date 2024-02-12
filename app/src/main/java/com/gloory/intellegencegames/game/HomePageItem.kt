package com.gloory.intellegencegames.game

import com.gloory.intellegencegames.R

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 11.02.2024               │
//└──────────────────────────┘
enum class HomePageItem(val imageId: Int, val text: String) {
    SUDOKU(
        R.drawable.sudoku_icon,
        "Sudoku"
    ),
    ESLESTIRME(
        R.drawable.memorygame_icon,
        "Eşleştirme"
    ),
    TICTACTOE(
        R.drawable.tictactoe_icon,
        "TicTacToe"
    );
    companion object{
        fun getAllList(): List<HomePageItem> {
            return listOf(SUDOKU,ESLESTIRME,TICTACTOE)
        }
    }
}