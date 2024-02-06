package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentTicTacToeBinding
import com.gloory.intellegencegames.databinding.TictactoDifficultyScreenBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class TicTacToeFragment : Fragment() {
    private var _binding: FragmentTicTacToeBinding? = null
    private val binding get() = _binding!!

    private var currentPlayer = 1 // 1 for X, 2 for O
    private lateinit var gameBoard: Array<Array<Int>>
    private var gridSize = 3 // Default grid size

    private var buttons: MutableMap<String, Button> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTicTacToeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog()
    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val bindingDialog =
            TictactoDifficultyScreenBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.apply {
            easyImage.setOnClickListener {
                gridSize = 3
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
            mediumImage.setOnClickListener {
                gridSize = 4
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
            hardImage.setOnClickListener {
                gridSize = 5
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.show()
    }

    private fun createGameboard(size: Int) {
        gameBoard = Array(size) { Array(size) { 0 } }  //initialize game
        val gridLayout = GridLayout(requireContext())
        gridLayout.layoutParams = GridLayout.LayoutParams().apply {
            width = GridLayout.LayoutParams.MATCH_PARENT
            height = GridLayout.LayoutParams.WRAP_CONTENT
        }
        gridLayout.columnCount = size
        gridLayout.rowCount = size
        for (i in 0 until size) {
            for (j in 0 until size) {
                val button = Button(requireContext())
                button.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(j, 1f)
                    rowSpec = GridLayout.spec(i, 1f)
                    setMargins(8, 8, 8, 8)
                }
                button.setOnClickListener {
                    onCellClicked(i, j, button)
                }
                button.text = ""
                gridLayout.addView(button)
                buttons["$i$j"] = button
            }
        }
        binding.tictactoeGrid.removeAllViews()
        binding.tictactoeGrid.addView(gridLayout)

    }

    private fun decideStartingPlayer() {
        currentPlayer = (1..2).random()
        if (currentPlayer == 2) {
            makeComputerMove()
        } else {
            Log.d("TicTacToeFragment", "Oyuncu başlıyor. Sıra oyuncuda.")
        }
    }


    private val handler = Handler(Looper.getMainLooper())

    private fun onCellClicked(row: Int, col: Int, button: Button) {
        if (gameBoard[row][col] == 0) { //boş mu?
            gameBoard[row][col] = currentPlayer
            button.text = if (currentPlayer == 1) "X" else "O"
            checkWin(row, col)
            currentPlayer = if (currentPlayer == 1) 2 else 1
           disableAllButtons()
            handler.postDelayed({
                if (currentPlayer == 2) {
                    Log.d("TicTacToeFragment", " Sıra PC de.")
                    makeComputerMove()
                }
            }, 2000)
        }
    }

    private fun disableAllButtons() {
        buttons.forEach { (_, button) ->
            button.isEnabled = false
        }
    }

    private fun enableAllButtons() {
        buttons.forEach { (_, button) ->
            button.isEnabled = true
        }
    }

    //bilgisayarın yapay zeka stratejisi kullanıldı
    private fun makeComputerMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()

        for (i in gameBoard.indices) {
            for (j in gameBoard[i].indices) {
                if (gameBoard[i][j] == 0) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        if (emptyCells.isNotEmpty()) {
            val randomIndex = (0 until emptyCells.size).random()
            val (row, col) = emptyCells[randomIndex]

            val view = buttons["$row$col"]
            if (view != null && view is Button) {
                val button = view
                gameBoard[row][col] = currentPlayer
                button.text = if (currentPlayer == 1) "X" else "O"
                checkWin(row, col)
                currentPlayer = if (currentPlayer == 1) 2 else 1
            } else {
                handleException("Button not found for row: $row, col: $col")
            }
            enableAllButtons()
        }
    }

    private fun handleException(message: String) {
        // Hata durumuyla ilgili işlemler yapılabilir, örneğin loglama veya kullanıcıya bilgilendirme
        Log.e("TicTacToeFragment", message)
    }

    private fun checkWin(row: Int, col: Int) {
        val currentPlayerValue = gameBoard[row][col] //şuanki oyun durumu

        // Yatay kontrol
        var count = 0
        for (i in 0 until gridSize) {
            if (gameBoard[row][i] == currentPlayerValue) { // Şu anki oyuncunun aynı değere sahip olduğu hücreleri say
                count++
            }
        }
        if (count == gridSize) {
            // Yatayda kazanma durumu
            announceWinner(currentPlayerValue)
            return
        }
        // Dikey kontrol
        count = 0
        for (i in 0 until gridSize) {
            if (gameBoard[i][col] == currentPlayerValue) { // Şu anki oyuncunun aynı değere sahip olduğu hücreleri say
                count++
            }
        }
        if (count == gridSize) {
            // Dikeyde kazanma durumu
            announceWinner(currentPlayerValue)
            return
        }

// Çapraz kontrol (sol üstten sağ alta)
        if (row == col) {
            count = 0
            for (i in 0 until gridSize) {
                if (gameBoard[i][i] == currentPlayerValue) { // Şu anki oyuncunun aynı değere sahip olduğu hücreleri say
                    count++
                }
            }
            if (count == gridSize) {
                // Çaprazda kazanma durumu
                announceWinner(currentPlayerValue)
                return
            }
        }

        // Çapraz kontrol (sağ üstten sol alta)
        if (row + col == gridSize - 1) {
            count = 0
            for (i in 0 until gridSize) {
                if (gameBoard[i][gridSize - 1 - i] == currentPlayerValue) { // Şu anki oyuncunun aynı değere sahip olduğu hücreleri say
                    count++
                }
            }
            if (count == gridSize) {
                // Çaprazda kazanma durumu
                announceWinner(currentPlayerValue)
                return
            }
        }
        // Beraberlik kontrolü
        var draw = true
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (gameBoard[i][j] == 0) { // Oyun tahtasında boş hücre var mı kontrol et
                    draw = false
                    break
                }
            }
        }
        if (draw) {
            announceDraw()
        }
    }

    //beraberlik
    private fun announceDraw() {
        val drawText = "Oyun berabere kaldı"

        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(drawText)
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
                showDialog()
            }
            .setNegativeButton("Yeniden Oyna") { dialog, _ ->
                dialog.dismiss()
                resetGame()
            }
            .create()

        alertDialog.show()
    }

    private fun resetGame() {
        createGameboard(gridSize)
    }

    //kazanan
    private fun announceWinner(player: Int) {
        val winner = if (player == 1) "X" else "O"
        val winnerText = "Kazanan: $winner"

        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(winnerText)
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
                showDialog()
            }
            .setNegativeButton("Yeniden Oyna") { dialog, _ ->
                dialog.dismiss()
                resetGame()
            }
            .create()

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}