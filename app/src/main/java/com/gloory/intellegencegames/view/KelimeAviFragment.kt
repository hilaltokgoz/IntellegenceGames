package com.gloory.intellegencegames.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentKelimeAviBinding
import kotlin.random.Random

class KelimeAviFragment : Fragment() {
    private var _binding: FragmentKelimeAviBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog
    private lateinit var selectedWords: List<String>
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private var elapsedTime = 0L
    private var gameFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKelimeAviBinding.inflate(inflater, container, false)
        val view = binding.root

        val selectedList = selectRandomList()
        val gridSize = when (selectedList) {
            is ShortWordList -> 10
            is NormalWordList -> 12
            is LongWordList -> 15
        }
        timerTextView = view.findViewById(R.id.timerTextView)

        println("Seçilen liste: ${selectedList.javaClass.simpleName}")

        val randomWords = selectRandomWords(selectedList)
        println("Seçilen kelimeler: $randomWords")

        addTextViewsToLinearLayout(randomWords)
        createGridLayout(gridSize, randomWords)
        addTouchListenerToGridLayout()

        startTimer()

        return view
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime += 1000
                val minutes = (elapsedTime / 1000) / 60
                val seconds = (elapsedTime / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // Timer finished
            }
        }
        countDownTimer.start()
    }

    private fun stopTimer() {
        countDownTimer.cancel()
    }

    private fun selectRandomList(): ListType {
        return when ((0 until 3).random()) {
            0 -> ShortWordList
            1 -> NormalWordList
            else -> LongWordList
        }
    }

    //seçilen listeden random beş kelime seçer.
    private fun selectRandomWords(selectedList: ListType): List<String> {
        val words = selectedList.words.toMutableList()
        val randomWords = mutableListOf<String>()
        repeat(5) {
            val randomIndex = (0 until words.size).random()
            val selectedWord = words.removeAt(randomIndex) // Seçilen kelimeyi listeden kaldır
            randomWords.add(selectedWord)
        }
        return randomWords
    }

    private fun addTextViewsToLinearLayout(randomWords: List<String>) {
        val linearLayout = binding.linearLayout
        linearLayout.removeAllViews()

        for (word in randomWords) {
            val textView = TextView(requireContext()).apply {
                text = word
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = resources.getDimensionPixelSize(R.dimen.textview_margin)
                }
            }
            linearLayout.addView(textView)
        }
    }

    private fun createGridLayout(gridSize: Int, words: List<String>) {
        val gridLayout = binding.gridLayout
        gridLayout.removeAllViews()
        gridLayout.rowCount = gridSize
        gridLayout.columnCount = gridSize

        val grid = Array(gridSize) { Array(gridSize) { "" } }

        for (word in words) {
            placeWordInGrid(grid, word)
        }

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (grid[i][j].isEmpty()) {
                    grid[i][j] = getRandomLetter()
                }
                val textView = TextView(requireContext()).apply {
                    text = grid[i][j]
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(j, 1, GridLayout.FILL, 1f)
                        rowSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1f)
                    }
                }
                gridLayout.addView(textView)

            }
        }

    }

    private fun placeWordInGrid(grid: Array<Array<String>>, word: String) {
        val gridSize = grid.size
        val directions = listOf(
            Direction.LEFT_TO_RIGHT,
            Direction.RIGHT_TO_LEFT,
            Direction.TOP_TO_BOTTOM,
            Direction.BOTTOM_TO_TOP
        )

        var placed = false

        while (!placed) {
            val direction = directions.random()
            val startRow = Random.nextInt(gridSize)
            val startCol = Random.nextInt(gridSize)

            if (canPlaceWord(grid, word, startRow, startCol, direction)) {
                placeWord(grid, word, startRow, startCol, direction)
                placed = true
            }
        }
    }

    private fun canPlaceWord(
        grid: Array<Array<String>>,
        word: String,
        row: Int,
        col: Int,
        direction: Direction
    ): Boolean {
        val gridSize = grid.size

        return when (direction) {
            Direction.LEFT_TO_RIGHT -> col + word.length <= gridSize && (word.indices).all { grid[row][col + it].isEmpty() }
            Direction.RIGHT_TO_LEFT -> col - word.length >= -1 && (word.indices).all { grid[row][col - it].isEmpty() }
            Direction.TOP_TO_BOTTOM -> row + word.length <= gridSize && (word.indices).all { grid[row + it][col].isEmpty() }
            Direction.BOTTOM_TO_TOP -> row - word.length >= -1 && (word.indices).all { grid[row - it][col].isEmpty() }
        }
    }

    private fun placeWord(
        grid: Array<Array<String>>,
        word: String,
        row: Int,
        col: Int,
        direction: Direction
    ) {
        when (direction) {
            Direction.LEFT_TO_RIGHT -> word.forEachIndexed { index, c ->
                grid[row][col + index] = c.toString()
            }
            Direction.RIGHT_TO_LEFT -> word.forEachIndexed { index, c ->
                grid[row][col - index] = c.toString()
            }
            Direction.TOP_TO_BOTTOM -> word.forEachIndexed { index, c ->
                grid[row + index][col] = c.toString()
            }
            Direction.BOTTOM_TO_TOP -> word.forEachIndexed { index, c ->
                grid[row - index][col] = c.toString()
            }
        }
    }

    private fun getRandomLetter(): String {
        val alphabet = ('A'..'Z')
        return alphabet.random().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        // Check if the fragment is attached to activity before showing the dialog
        if (isAdded) {
            showGameFinishedDialog(timerTextView)
        }
        _binding = null
    }
    //gridLayout dokunabilirlik

    @SuppressLint("ClickableViewAccessibility")
    private fun addTouchListenerToGridLayout() {
        val gridLayout = binding.gridLayout
        val selectedCells = mutableListOf<TextView>()
        var originalBackgroundColor: Drawable? = null

        gridLayout.forEach { textView ->
            textView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.performClick()
                        if (!selectedCells.contains(v)) {
                            originalBackgroundColor = v.background
                            selectedCells.add(v as TextView)
                            v.setBackgroundColor(Color.parseColor("#800000FF"))
                        }
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val childView =
                            getViewAtPosition(gridLayout, event.rawX.toInt(), event.rawY.toInt())
                        if (childView != null && childView is TextView && !selectedCells.contains(
                                childView
                            )
                        ) {
                            selectedCells.add(childView)
                            childView.setBackgroundColor(Color.parseColor("#800000FF"))
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val selectedWord = selectedCells.joinToString("") { it.text.toString() }
                        if (checkWord(selectedWord)) {
                            selectedCells.forEach { it.setBackgroundColor(Color.parseColor("#8000FF00")) } // 80 is the alpha value (50% transparency)
                            strikeThroughWordInLinearLayout(selectedWord)
                            checkGameFinished()
                        } else {
                            selectedCells.forEach { it.background = originalBackgroundColor }
                        }
                        selectedCells.clear()
                        true
                    }
                    else -> false
                }
            }
            textView.setOnClickListener {

            }
        }
    }

    private fun getViewAtPosition(parent: ViewGroup, x: Int, y: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val location = IntArray(2)
            child.getLocationOnScreen(location)
            if (x >= location[0] && x <= location[0] + child.width && y >= location[1] && y <= location[1] + child.height) {
                return child
            }
        }
        return null
    }

    private fun checkWord(word: String): Boolean {
        val words = ShortWordList.words + NormalWordList.words + LongWordList.words
        return words.contains(word)
    }

    private fun strikeThroughWordInLinearLayout(word: String) {
        val linearLayout = binding.linearLayout

        for (i in 0 until linearLayout.childCount) {
            val textView = linearLayout.getChildAt(i) as TextView
            if (textView.text == word) {
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private fun checkGameFinished() {
        val linearLayout = binding.linearLayout
        var allWordsFound = true

        for (i in 0 until linearLayout.childCount) {
            val textView = linearLayout.getChildAt(i) as TextView
            if (textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG == 0) {
                allWordsFound = false
                break
            }
        }

        if (allWordsFound && !gameFinished) {
            gameFinished = true
            stopTimer()
            showGameFinishedDialog(timerTextView)
        }
    }

    private fun showGameFinishedDialog(timerTextView: TextView) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_kelimeavi, null)
        val textViewTimer = dialogView.findViewById<TextView>(R.id.textViewTimer)
        val positiveButton = dialogView.findViewById<Button>(R.id.positiveButton)
        val negativeButton = dialogView.findViewById<Button>(R.id.negativeButton)

        builder.setView(dialogView)
        val dialog = builder.create()
        textViewTimer.text = "Süre: ${timerTextView.text}"

        positiveButton.setOnClickListener {
            resetGame()
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            findNavController().navigate(R.id.action_kelimeAviFragment_to_homeFragment)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun resetGame() {
        gameFinished = false
        elapsedTime = 0L
        val selectedList = selectRandomList()
        val gridSize = when (selectedList) {
            is ShortWordList -> 10
            is NormalWordList -> 12
            is LongWordList -> 15
        }
        val randomWords = selectRandomWords(selectedList)

        addTextViewsToLinearLayout(randomWords)
        createGridLayout(gridSize, randomWords)
        addTouchListenerToGridLayout()
        startTimer()

    }
}

enum class Direction {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
}

sealed class ListType {
    abstract val words: List<String>
}

object ShortWordList : ListType() {
    override val words = listOf(
        "ANNE", "ARA", "ATA", "BABA", "CEZA", "DAVA", "EFE",
        "ADANA", "BALIK", "CUMA", "DURU", "ELMA",
        "AFRİKA", "BAKIR", "CEYLAN", "DENİZ", "FETHİYE", "BURSA", "DÜNYA", "ELAZIĞ",
        "FINDIK", "BALTA", "CEPHE", "DOLAP", "EŞREF", "ELMA", "ARMUT", "KİRAZ", "KALEM",
        "ŞEKER", "TUZ", "UN", "PİRİNÇ", "BULGUR", "MAKARNA", "YEREL", "GLOBAL",
        "TOPLUM", "YAKIN", "UZAK",
    )
}

object NormalWordList : ListType() {
    override val words = listOf(
        "AKDENİZ", "BEYKOZ", "DALGA", "EKVATOR", "ANKARA", "BERLİN", "CETVEL", "DOKTOR", "FELEMENK",
        "BİSKÜVİ", "CANAVAR", "DARBE", "EŞYA", "ISPANAK", "FASULYE", "MERCİMEK", "BARBUNYA",
        "GELECEK", "BROKOLİ", "PIRASA", "SARIMSAK", "BEZELYE", "PATLICAN", "SALATALIK",
        "MÜHENDİSLİK", "YÖNETİM", "DANIŞMAN"
    )
}

object LongWordList : ListType() {
    override val words = listOf(
        "MUHTEŞEM", "FANTASTİK", "HARİKULADE", "MUCİZEVİ", "OLAĞANÜSTÜ",
        "UMUTSUZLUK", "YAKLAŞIM", "KARAMSARLIK"
    )
}