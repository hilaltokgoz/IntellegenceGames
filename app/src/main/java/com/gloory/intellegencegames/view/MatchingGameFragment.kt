package com.gloory.intellegencegames.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentMatchingGameBinding

class MatchingGameFragment : Fragment() {
    private var _binding: FragmentMatchingGameBinding? = null
    private val binding get() = _binding!!

    val images = mutableListOf(
        R.drawable.bat,
        R.drawable.butterfly,
        R.drawable.dove,
        R.drawable.panda,
        R.drawable.parrot,
        R.drawable.spider,
    )
    private lateinit var imageViews: List<ImageButton>
    private var selectedImages = mutableListOf<Int>()
    private var matchedPairs = 0 //eşleşen çiftler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchingGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            imageViews = listOf(
                button, button1, button2, button3, button4, button5, button6,
                button7, button8, button9, button10, button11
            )
        }
        resetGame()
    }

    //Random resimler seçilecek.
    private fun resetGame() {
        val selectedImagesList = mutableListOf<Int>()
        for (i in 0 until imageViews.size / 2) {
            val randomImage = images.random()
            selectedImagesList.add(randomImage)
            selectedImagesList.add(randomImage)
        }
        selectedImages = selectedImagesList.shuffled().toMutableList()
        // ImageView'ları sıfırla
        for ((index, imageView) in imageViews.withIndex()) {
            imageView.setImageResource(R.drawable.gamecontroller)
            imageView.tag = index
            imageView.setOnClickListener { onImageClicked(it) }
        }
        matchedPairs = 0
    }

    private fun onImageClicked(view: View) {
        val imageView = view as ImageView
        val position = imageView.tag as Int
        val selectedImage = selectedImages[position]

        imageView.setImageResource(selectedImage)
        imageView.setOnClickListener(null)

        if (selectedImages.size == 2) {
            if (selectedImages[0] == selectedImages[1]) {// Eşleşme var
                matchedPairs++
                if (matchedPairs == imageViews.size / 2) {
                    // Oyun tamamlandı
                    Toast.makeText(requireContext(), "Oyun tamamlandı!", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({ resetGame() }, 1000)
                }
            } else {// Eşleşme yok, beklet ve geri çevir
                Handler(Looper.getMainLooper()).postDelayed({
                    imageViews.forEach {
                        it.setImageResource(R.drawable.gamecontroller)
                        it.setOnClickListener { onImageClicked(it) }
                    }
                }, 1000)
            }
            selectedImages.clear()
        } else { // İkinci resmi bekliyoruz
            selectedImages.add(selectedImage)
        }
    }


}

