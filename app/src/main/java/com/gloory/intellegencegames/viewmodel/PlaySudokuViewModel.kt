package com.gloory.intellegencegames.viewmodel

import androidx.lifecycle.ViewModel
import com.gloory.intellegencegames.game.SudokuGame


// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 1.12.2023                │
//└──────────────────────────┘

//Activity veya LifeCycle'a bağlanarak bilgileri depolar
//Ekran orietaion'ı  değiştiğnde veri kaybını önlemek amacıyla oluşturulur.
//sudoku oyun durumu burada saklanacak
class  PlaySudokuViewModel:ViewModel(){
val sudokuGame= SudokuGame()
}