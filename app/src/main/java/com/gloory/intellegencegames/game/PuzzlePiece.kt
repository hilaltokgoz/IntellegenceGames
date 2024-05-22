package com.gloory.intellegencegames.game

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 21.05.2024               │
//└──────────────────────────┘

class PuzzlePiece (context:Context?) : AppCompatImageView(context!!) {
    var xCoord = 0
    var yCoord = 0
    var pieceWidth = 0
    var pieceHeight = 0
    var canMove = true

}