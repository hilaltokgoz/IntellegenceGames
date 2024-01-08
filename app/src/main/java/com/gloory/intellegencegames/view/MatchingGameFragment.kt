package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentMatchingGameBinding


class MatchingGameFragment : Fragment() {
    private var _binding: FragmentMatchingGameBinding? = null
    private val binding get() = _binding!!
    var selectedDifficulty = 0

    private lateinit var currentShuffledImages: List<Int>

    private var flippedPositions = mutableSetOf<Int>()
    private var matchedPairs = 0

    val images = listOf(
        R.drawable.sports_handball,
        R.drawable.sports_tennis,
        R.drawable.theater_comedy,
        R.drawable.timer,
        R.drawable.tsunami,
        R.drawable.weekend,
        R.drawable.whatshot,
        R.drawable.wind_power,
        R.drawable.cookie,
        R.drawable.pool,
        R.drawable.heart_broken,
        R.drawable.toys,
        R.drawable.tv,
        R.drawable.train,
        R.drawable.two_wheeler,
        R.drawable.vaccines
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
                        val imageCount = (3 * 4) / 2
                        val selectedImages = images.shuffled().filterIndexed { index, i ->
                            index < imageCount
                        }
                        currentShuffledImages = (selectedImages + selectedImages).shuffled()
                        addImage(3, 4)
                    }
                    1 -> {
                        val imageCount = (4 * 5) / 2
                        val selectedImages = images.shuffled().filterIndexed { index, i ->
                            index < imageCount
                        }
                        currentShuffledImages = (selectedImages + selectedImages).shuffled()
                        addImage(4, 5)
                    }
                    2 -> {
                        val imageCount = (5 * 6) / 2
                        val selectedImages = images.shuffled().filterIndexed { index, i ->
                            index < imageCount
                        }
                        currentShuffledImages = (selectedImages + selectedImages).shuffled()
                        addImage(5, 6)
                    }
                    else -> {
                        val imageCount = (3 * 4) / 2
                        val selectedImages = images.shuffled().filterIndexed { index, i ->
                            index < imageCount
                        }
                        currentShuffledImages = (selectedImages + selectedImages).shuffled()
                        addImage(3, 4)
                    }
                }
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun addImage(rowCount: Int, columnCount: Int) {
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                val imageView = ImageView(requireContext())
                imageView.setImageResource(R.drawable.back_image)

                val index = i * columnCount + j
                imageView.tag = index

                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                imageView.layoutParams = params
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.layoutParams.width = binding.mainGrid.width / columnCount
                imageView.layoutParams.height = binding.mainGrid.width / rowCount

                imageView.setOnClickListener { onImageClicked(imageView) }
                binding.mainGrid.addView(imageView)
            }
        }
    }

    private fun onImageClicked(imageView: ImageView) {
        val currentPosition = imageView.tag as Int

        if (flippedPositions.size < 2 && currentPosition !in flippedPositions) {
            flippedPositions.add(currentPosition)
            imageView.setImageResource(currentShuffledImages[currentPosition])
            if (flippedPositions.size == 2) {
                checkMatch(binding.mainGrid.columnCount, binding.mainGrid.rowCount)
            }
        }
    }

    private fun checkMatch(columnCount: Int, rowCount:Int) {
        val positions = flippedPositions.toList()
        val indices = positions.map { it % columnCount + it / columnCount * columnCount }
        val image1 = currentShuffledImages[indices[0]]
        val image2 = currentShuffledImages[indices[1]]

        if (image1 == image2) {
            // Eşleşme durumu
            flippedPositions.clear()
            matchedPairs++

            println("matchedPairs: $matchedPairs")
            println("Expected pairs: ${columnCount*rowCount/2 }") //8

            if (matchedPairs == (columnCount*rowCount)/2 ) {
                // Oyun tamamlandı
                flippedPositions.clear()
                    resultScreenDialog()

            }
        } else {
            // Eşleşmeme durumu
            binding.mainGrid.postDelayed({     // Resimleri ters çevir ve bekle
                for (position in flippedPositions) {
                    val imageView = binding.mainGrid.getChildAt(position) as ImageView
                    imageView.setImageResource(R.drawable.back_image)
                }
                flippedPositions.clear()
            }, 1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resultScreenDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog
            .setTitle("TEBRİKLER OYUNU TAMAMLADINIZ")
            .setNegativeButton("Çıkış") { dialog, which ->
                // HomeFragment'a dön
                matchedPairs=0
                flippedPositions.clear()
                findNavController().navigate(R.id.action_matchingGameFragment_to_homeFragment)
            }
            .setPositiveButton("Yeniden Oyna") { dialog, which ->
                matchedPairs=0
                flippedPositions.clear()
                showAlertDialog()    //Zorluk seçme dialoğu göster
            }
        val dialog: AlertDialog = alertDialog.create()
        dialog.show()

    }

}




