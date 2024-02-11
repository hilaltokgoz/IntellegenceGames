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

object HomePage_Database {

    fun getList(): ArrayList<HomePage_ItemViewModel> {

        val gameSudoku = HomePage_ItemViewModel(
            R.drawable.ic_sudoku,
            "Sudoku Oyunu",
        )
        val gameMatching = HomePage_ItemViewModel(
            R.drawable.ic_baseline_matching_game,
            "Eşleştirme Oyunu",
        )
        val gameTicTacToe = HomePage_ItemViewModel(
            R.drawable.timer,
            "TicTacToe Oyunu",
        )

        val getList: ArrayList<HomePage_ItemViewModel> = ArrayList()
        getList.add(gameSudoku)
        getList.add(gameMatching)
        getList.add(gameTicTacToe)

        return getList
    }
}
