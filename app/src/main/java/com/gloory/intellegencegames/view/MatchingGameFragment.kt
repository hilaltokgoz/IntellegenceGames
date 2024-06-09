package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentMatchingGameBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class MatchingGameFragment : Fragment() {
    private var _binding: FragmentMatchingGameBinding? = null
    private val binding get() = _binding!!
    var selectedDifficulty = 0

    private lateinit var currentShuffledImages: List<Int>

    private var flippedPositions = mutableSetOf<Int>()
    private var matchedPairs = 0

    private var elapsedTimeMillis: Long = 0
    private var timer: Runnable? = null

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTimer.visibility = View.GONE
        showDifficultyBottomSheetDialog()
    }


    private fun showDifficultyBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val dialogView = layoutInflater.inflate(R.layout.difficulty_screen_dialog, null)
        bottomSheetDialog.setContentView(dialogView)

        val easyLayout = dialogView.findViewById<LinearLayout>(R.id.easyLayout)
        val mediumLayout = dialogView.findViewById<LinearLayout>(R.id.mediumLayout)
        val hardLayout = dialogView.findViewById<LinearLayout>(R.id.hardLayout)

        val onClickListener = { difficulty: Int ->
            when (difficulty) {
                0 -> {
                    val imageCount = (3 * 4) / 2
                    val selectedImages = images.shuffled().filterIndexed { index, _ -> index < imageCount }
                    currentShuffledImages = (selectedImages + selectedImages).shuffled()
                    addImage(3, 4)
                }
                1 -> {
                    val imageCount = (4 * 5) / 2
                    val selectedImages = images.shuffled().filterIndexed { index, _ -> index < imageCount }
                    currentShuffledImages = (selectedImages + selectedImages).shuffled()
                    addImage(4, 5)
                }
                2 -> {
                    val imageCount = (5 * 6) / 2
                    val selectedImages = images.shuffled().filterIndexed { index, _ -> index < imageCount }
                    currentShuffledImages = (selectedImages + selectedImages).shuffled()
                    addImage(5, 6)

                }
            }
            bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            startGameTimer()
            bottomSheetDialog.dismiss()
        }
        easyLayout.setOnClickListener {
            onClickListener(0)
        }
        mediumLayout.setOnClickListener {
            onClickListener(1)
        }
        hardLayout.setOnClickListener {
            onClickListener(2)
        }

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }

    private fun startGameTimer() {
        binding.tvTimer.visibility = View.VISIBLE
        timer = object : Runnable {
            override fun run() {
                elapsedTimeMillis += 1000
                updateElapsedTime()
                binding.root.postDelayed(this, 1000)
            }
        }
        binding.root.postDelayed(timer!!, 1000)
    }

    private fun updateElapsedTime() {
        val minutes = (elapsedTimeMillis / 1000) / 60
        val seconds = (elapsedTimeMillis / 1000) % 60
        val timeElapsedFormatted = String.format("Süre: %02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeElapsedFormatted
    }

    fun addImage(rowCount: Int, columnCount: Int) {
        // Get the screen width
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Calculate the new image size
        val widthFactor = 1.2
        val heightFactor = 1.2
        val imageWidth = (screenWidth / columnCount * widthFactor).toInt()
        val imageHeight = (screenWidth / rowCount * heightFactor).toInt()

        // Ensure the images fit within the screen width
        val maxImageWidth = screenWidth / columnCount
        val maxImageHeight = screenWidth / rowCount
        val finalImageWidth = minOf(imageWidth, maxImageWidth)
        val finalImageHeight = minOf(imageHeight, maxImageHeight)

        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                val imageView = ImageView(requireContext())
                imageView.setImageResource(R.drawable.question_mark)

                val index = i * columnCount + j
                imageView.tag = index

                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                imageView.layoutParams = params
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                imageView.layoutParams.width = finalImageWidth
                imageView.layoutParams.height = finalImageHeight

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

    private fun checkMatch(columnCount: Int, rowCount: Int) {
        val positions = flippedPositions.toList()
        val indices = positions.map { it % columnCount + it / columnCount * columnCount }
        val image1 = currentShuffledImages[indices[0]]
        val image2 = currentShuffledImages[indices[1]]

        if (image1 == image2) {
            // Eşleşme durumu
            flippedPositions.clear()
            matchedPairs++
            println("matchedPairs: $matchedPairs")
            println("Expected pairs: ${columnCount * rowCount / 2}") //8
            if (matchedPairs == (columnCount * rowCount) / 2) {
                // Oyun tamamlandı
                showResultScreenDialog()
                timer?.let { binding.root.removeCallbacks(it) }
            }
        } else {
            // Eşleşmeme durumu
            binding.mainGrid.postDelayed({     // Resimleri ters çevir ve bekle
                for (position in flippedPositions) {
                    val imageView = binding.mainGrid.getChildAt(position) as ImageView
                    imageView.setImageResource(R.drawable.question_mark)
                }
                flippedPositions.clear()
            }, 1000)
        }
    }

    private fun showResultScreenDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_game_completed, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val playAgainButton = dialogView.findViewById<Button>(R.id.btn_play_again)
        val exitButton = dialogView.findViewById<Button>(R.id.btn_exit)
        val timerTextView = dialogView.findViewById<TextView>(R.id.tv_timer_result)

        val minutes = (elapsedTimeMillis / 1000) / 60
        val seconds = (elapsedTimeMillis / 1000) % 60
        val timeElapsedFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text =  "Süre: $timeElapsedFormatted"
        timerTextView.visibility = View.VISIBLE

        playAgainButton.setOnClickListener {
            alertDialog.dismiss()
            resetGame()
            binding.tvTimer.visibility = View.GONE
            showDifficultyBottomSheetDialog()    //Zorluk seçme dialoğu göster
        }

        exitButton.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
            timer?.let {
                binding.root.removeCallbacks(it) // Timer'ı kaldır
                timer = null // Timer'ı null olarak işaretle
            }
        }
    }
    private fun resetGame() {
        binding.mainGrid.removeAllViews() //image'ler kaldırıldı.
        flippedPositions.clear()
        matchedPairs = 0
        elapsedTimeMillis = 0 // Süreyi sıfırla
        binding.tvTimer.visibility = View.VISIBLE
        binding.tvTimer.text = "00:00" // Süreyi sıfırla

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.let {
            it.root.removeCallbacks(timer) // Timer'ı kaldır
            timer = null // Timer'ı null olarak işaretle
            _binding = null
        }
    }
}




