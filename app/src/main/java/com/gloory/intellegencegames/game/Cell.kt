package com.gloory.intellegencegames.game

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 4.12.2023                │
//└──────────────────────────┘

//Hücre Class'ı depolamak için kullanılacaktır.
//

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false
)