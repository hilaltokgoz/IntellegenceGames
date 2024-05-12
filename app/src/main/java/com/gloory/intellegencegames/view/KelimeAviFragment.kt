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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.databinding.FragmentKelimeAviBinding

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
        createGridLayout(gridSize)
        println("Seçilen liste: ${selectedList.javaClass.simpleName}")

        return view
    }

    private fun selectRandomList(): ListType {
        return when ((0 until 3).random()) {
            0 -> ShortWordList
            1 -> NormalWordList
            else -> LongWordList
        }
    }

    private fun createGridLayout(gridSize: Int) {
        binding.gridLayout.rowCount = gridSize
        binding.gridLayout.columnCount = gridSize

        val margin = 2.dpToPx(requireContext())

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val textView = TextView(requireContext()).apply {
                    text = getRandomLetter()
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

                binding.gridLayout.addView(textView)

            }

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


sealed class ListType {
    abstract val words: List<String>
}

//3-4-5-6 harf
object ShortWordList : ListType() {
    override val words = listOf(
        "ana", "ara", "ata", "baba", "ceza", "dava", "efe",
        "adana", "balık", "cuma", "duru", "elma",
        "afrika", "bakır", "ceylan", "deniz", "fethiye", "bursa", "dünya", "elazığ",
        "fındık", "balta", "cephe", "dolap", "eşref", "elma", "armut", "kiraz", "kalem",
        "şeker", "tuz", "un", "pirinç", "bulgur", "makarna", "yerel", "global",
        "toplum", "yakın", "uzak",
    )
}

object NormalWordList : ListType() {
    override val words = listOf(
        "akdeniz", "beykoz", "dalga", "ekvator", "ankara", "berlin", "cetvel", "doktor", "felemenk",
        "bisküvi", "canavar", "darbe", "eşyalar", "ıspanak", "fasulye", "mercimek", "barbunya",
        "gelecek", "brokoli", "pırasa", "sarımsak", "bezelye", "patlıcan", "salatalık",
        "mühendislik", "yönetim", "danışman"
    )
}

object LongWordList : ListType() {
    override val words = listOf(
        "muhtesemlik", "fantastik", "harikulade", "mucizevi", "olaganustu",
        "umutsuzluk", "yaklasimlar", "karamsarlik"
    )
}