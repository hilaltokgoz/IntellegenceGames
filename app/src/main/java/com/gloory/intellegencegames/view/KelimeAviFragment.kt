package com.gloory.intellegencegames.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentKelimeAviBinding
import kotlin.random.Random


class KelimeAviFragment : Fragment() {
    private var _binding: FragmentKelimeAviBinding? = null
    private val binding get() = _binding!!

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

        println("Seçilen liste: ${selectedList.javaClass.simpleName}")

        val randomWords = selectRandomWords(selectedList)
        println("Seçilen kelimeler: $randomWords")

        addTextViewsToLinearLayout(randomWords)
        createGridLayout(gridSize,randomWords)

        return view
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

    private fun createGridLayout(gridSize: Int,words: List<String>) {
        val gridLayout = binding.gridLayout
        gridLayout.rowCount = gridSize
        gridLayout.columnCount = gridSize

        val margin = 2.dpToPx(requireContext())
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
                        setMargins(margin, margin, margin, margin)
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
    private fun canPlaceWord(grid: Array<Array<String>>, word: String, row: Int, col: Int, direction: Direction): Boolean {
        val gridSize = grid.size

        return when (direction) {
            Direction.LEFT_TO_RIGHT -> col + word.length <= gridSize && (word.indices).all { grid[row][col + it].isEmpty() }
            Direction.RIGHT_TO_LEFT -> col - word.length >= -1 && (word.indices).all { grid[row][col - it].isEmpty() }
            Direction.TOP_TO_BOTTOM -> row + word.length <= gridSize && (word.indices).all { grid[row + it][col].isEmpty() }
            Direction.BOTTOM_TO_TOP -> row - word.length >= -1 && (word.indices).all { grid[row - it][col].isEmpty() }
        }
    }

    private fun placeWord(grid: Array<Array<String>>, word: String, row: Int, col: Int, direction: Direction) {
        when (direction) {
            Direction.LEFT_TO_RIGHT -> word.forEachIndexed { index, c -> grid[row][col + index] = c.toString() }
            Direction.RIGHT_TO_LEFT -> word.forEachIndexed { index, c -> grid[row][col - index] = c.toString() }
            Direction.TOP_TO_BOTTOM -> word.forEachIndexed { index, c -> grid[row + index][col] = c.toString() }
            Direction.BOTTOM_TO_TOP -> word.forEachIndexed { index, c -> grid[row - index][col] = c.toString() }
        }
    }

    private fun getRandomLetter(): String {
        val alphabet = ('A'..'Z')
        return alphabet.random().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private fun Int.dpToPx(requireContext: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        requireContext.resources.displayMetrics
    ).toInt()

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

//3-4-5-6 harf
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