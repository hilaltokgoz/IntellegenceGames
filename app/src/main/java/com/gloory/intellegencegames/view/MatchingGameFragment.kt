package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentMatchingGameBinding


class MatchingGameFragment : Fragment() {
    private var _binding: FragmentMatchingGameBinding? = null
    private val binding get() = _binding!!
    var selectedDifficulty = 0

    val images = mutableListOf(
        R.drawable.bat,
        R.drawable.butterfly,
        R.drawable.dove,
        R.drawable.panda,
        R.drawable.parrot,
        R.drawable.spider,
        R.drawable.bear,
        R.drawable.chicken,
        R.drawable.coala,
        R.drawable.deer,
        R.drawable.panda2,
        R.drawable.rabbit
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchingGameBinding.inflate(inflater, container, false)
        showAlertDialog()
        return binding.root
    }

    private fun showAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("Zorluk Seviyesini Seçiniz...")
            .setSingleChoiceItems(
                arrayOf("Kolay", "Orta", "Zor"), 0
            ) { _, which ->
                selectedDifficulty = which
            }
            .setPositiveButton("Tamam") { dialog, which ->
                when (selectedDifficulty) {
                    0 -> {
                        addImage(3, 3)
                    }
                    1 -> {
                        addImage(4, 4)
                    }
                    2 -> {
                        addImage(5, 5)
                    }
                    else -> {
                        addImage(3, 3)
                    }
                }
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun addImage(rowCount: Int, columnCount: Int) {
        val shuffledImage = images.shuffled()

        for (i in 0 until rowCount) {
            for (j in 0 until columnCount){
                val imageView = ImageView(requireContext())
                imageView.setImageResource(R.drawable.delete_black)

                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                imageView.layoutParams=params

                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                binding.mainGrid.addView(imageView)
            }
        }
    }

}



