package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.DifficultyScreenDialogBinding
import com.gloory.intellegencegames.databinding.FragmentTicTacToeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

//Alpha-Beta Pruning: ağaç aramasını daha verimli hale getirir. Bu algoritma da bilgisayarın daha
// iyi hamleler yapmasına olanak tanır.
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
            DifficultyScreenDialogBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.apply {
            easyLayout.setOnClickListener {
                gridSize = 3
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
            mediumLayout.setOnClickListener {
                gridSize = 4
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
            hardLayout.setOnClickListener {
                gridSize = 5
                createGameboard(gridSize)
                dialog.dismiss()
                decideStartingPlayer()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun createGameboard(size: Int) {
        gameBoard = Array(size) { Array(size) { 0 } }  //initialize game
        val gridLayout = GridLayout(requireContext())
        gridLayout.layoutParams = GridLayout.LayoutParams().apply {
            width = GridLayout.LayoutParams.MATCH_PARENT
            height = GridLayout.LayoutParams.MATCH_PARENT
        }
        gridLayout.columnCount = size
        gridLayout.rowCount = size
        for (i in 0 until size) {
            for (j in 0 until size) {
                val button = Button(requireContext())
                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(i, 1f),
                    GridLayout.spec(j, 1f)
                )
                layoutParams.width = 0
                layoutParams.height =
                    GridLayout.LayoutParams.WRAP_CONTENT // veya MATCH_PARENT olarak ayarlayabilirsin
                layoutParams.setMargins(8, 8, 8, 8)
                button.layoutParams = layoutParams
                button.setOnClickListener {
                    onCellClicked(i, j, button)
                }
                button.text = ""
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
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

        // 1. Kazanma stratejisi
        val winningMove = findWinningMove(emptyCells)
        if (winningMove != null) {
            makeMove(winningMove)
            return
        }

        // 2. Kazanma engelleme stratejisi
        val blockingMove = findBlockingMove(emptyCells)
        if (blockingMove != null) {
            makeMove(blockingMove)
            return
        }

        // 3. Orta nokta stratejisi
        if (gridSize % 2 != 0 && gameBoard[gridSize / 2][gridSize / 2] == 0) {
            makeMove(Pair(gridSize / 2, gridSize / 2))
            return
        }

        // 4. Köşe stratejisi
        val cornerMove = findCornerMove(emptyCells)
        if (cornerMove != null) {
            makeMove(cornerMove)
            return
        }

        // 5. Rastgele hamle stratejisi
        if (emptyCells.isNotEmpty()) {
            val randomIndex = (0 until emptyCells.size).random()
            val (row, col) = emptyCells[randomIndex]
            makeMove(Pair(row, col))
        }
    }

    // Kazanma stratejisi
    private fun findWinningMove(emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        for ((row, col) in emptyCells) {
            gameBoard[row][col] = currentPlayer
            if (isWinningMove(row, col)) {
                gameBoard[row][col] = 0
                return Pair(row, col)
            }
            gameBoard[row][col] = 0
        }
        return null
    }

    // Kazanma engelleme stratejisi
    private fun findBlockingMove(emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val opponent = if (currentPlayer == 1) 2 else 1
        for ((row, col) in emptyCells) {
            gameBoard[row][col] = opponent
            if (isWinningMove(row, col)) {
                gameBoard[row][col] = 0
                return Pair(row, col)
            }
            gameBoard[row][col] = 0
        }
        return null
    }

    // Orta nokta stratejisi
    private fun findCornerMove(emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val corners = listOf(Pair(0, 0), Pair(0, gridSize - 1), Pair(gridSize - 1, 0), Pair(gridSize - 1, gridSize - 1))
        for (corner in corners) {
            if (emptyCells.contains(corner)) {
                return corner
            }
        }
        return null
    }

    private fun makeMove(move: Pair<Int, Int>) {
        val (row, col) = move
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

    private fun isWinningMove(row: Int, col: Int): Boolean {
        val currentPlayerValue = gameBoard[row][col]

        // Yatay kontrol
        var count = 0
        for (i in 0 until gridSize) {
            if (gameBoard[row][i] == currentPlayerValue) {
                count++
            }
        }
        if (count == gridSize) {
            return true
        }

        // Dikey kontrol
        count = 0
        for (i in 0 until gridSize) {
            if (gameBoard[i][col] == currentPlayerValue) {
                count++
            }
        }
        if (count == gridSize) {
            return true
        }

        // Çapraz kontrol (sol üstten sağ alta)
        if (row == col) {
            count = 0
            for (i in 0 until gridSize) {
                if (gameBoard[i][i] == currentPlayerValue) {
                    count++
                }
            }
            if (count == gridSize) {
                return true
            }
        }

        // Çapraz kontrol (sağ üstten sol alta)
        if (row + col == gridSize - 1) {
            count = 0
            for (i in 0 until gridSize) {
                if (gameBoard[i][gridSize - 1 - i] == currentPlayerValue) {
                    count++
                }
            }
            if (count == gridSize) {
                return true
            }
        }

        return false
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

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_game_completed, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        alertDialog.show()

        val messageView = dialogView.findViewById<TextView>(R.id.tv_congratulations)
        messageView.text = drawText

        val positiveButton = dialogView.findViewById<Button>(R.id.btn_exit)
        val negativeButton = dialogView.findViewById<Button>(R.id.btn_play_again)

        val imageView = dialogView.findViewById<ImageView>(R.id.iv_congratulations)
        imageView.visibility = View.GONE // ImageView'ı gizle

        positiveButton.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }

        negativeButton.setOnClickListener {
            alertDialog.dismiss()
            showDialog()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 5.dpToPx(), 0) // Sağ kenarda 5dp margin ekliyoruz
        positiveButton.layoutParams = params
        negativeButton.layoutParams = params

    }

    //kazanan
    fun announceWinner(player: Int) {
        val winner = if (player == 1) "Sen" else "PC"
        val winnerText = "Kazanan:  $winner"

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_game_completed, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        alertDialog.show()

        val messageView = dialogView.findViewById<TextView>(R.id.tv_congratulations)
        messageView.text = if (player == 1) "Kazandınız" else "Kaybettiniz"

        val imageView = dialogView.findViewById<ImageView>(R.id.iv_congratulations)
        if (player == 1) {
            imageView.setImageResource(R.drawable.ic_celebration)
        } else {
            imageView.setImageResource(R.drawable.sad)
        }

        val exitButton = dialogView.findViewById<Button>(R.id.btn_exit)
        val newGameButton = dialogView.findViewById<Button>(R.id.btn_play_again)
        exitButton.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
        newGameButton.setOnClickListener {
            alertDialog.dismiss()
            showDialog()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 5.dpToPx(), 0) // Sağ kenarda 5dp margin ekliyoruz
        exitButton.layoutParams = params
        newGameButton.layoutParams = params
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}