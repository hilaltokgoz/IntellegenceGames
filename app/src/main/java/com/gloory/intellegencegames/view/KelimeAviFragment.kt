package com.gloory.intellegencegames.view

import android.graphics.Color
import android.os.Bundle
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

    val shortWordList = listOf("a", "c", "d", "e", "f")
    val normalWordList = listOf( "aa", "bb", "cc", "dd", "ee" ,"ff")
    val  longWordList = listOf("aaa", "bbb",  "ccc", "ddd", "eee", "fff")

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

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val textView = TextView(requireContext()).apply {
                    text = getRandomLetter()
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(j,1f)
                        rowSpec = GridLayout.spec(i,1f)
                    }
                }
                binding.gridLayout.addView(textView)

                if (j < gridSize ) {
                    val lineView = View(requireContext()).apply {
                        setBackgroundColor(resources.getColor(android.R.color.black))
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            2
                        )
                    }
                    binding.gridLayout.addView(lineView)

                }
             /**if (i < gridSize ) {
                    val verticalLineView = View(requireContext()).apply {
                        setBackgroundColor(resources.getColor(android.R.color.black))
                        layoutParams = ViewGroup.LayoutParams(
                            2,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }
                    binding.gridLayout.addView(verticalLineView)
                }
             **/

            }
        }

    }
    private fun getRandomLetter(): String {
        val alphabet = ('a'..'z')
        return alphabet.random().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

sealed class ListType
object ShortWordList : ListType()
object NormalWordList : ListType()
object LongWordList : ListType()