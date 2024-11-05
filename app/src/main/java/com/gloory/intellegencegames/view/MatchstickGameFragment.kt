package com.gloory.intellegencegames.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentMatchingGameBinding
import com.gloory.intellegencegames.databinding.FragmentMatchstickGameBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MatchstickGameFragment : Fragment() {

    private var _binding: FragmentMatchstickGameBinding? = null
    private val binding get() = _binding!!

    var totalMatchsticks = 0
    private var userSelectedCount = 0
    private val maxSelectable = 3
    private lateinit var matchstickViews: MutableList<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchstickGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDifficultyBottomSheetDialog()

        binding.btnUserMove.setOnClickListener {
            if (userSelectedCount > 0) {
                totalMatchsticks -= userSelectedCount
                userSelectedCount = 0
                checkGameStatus("Kullanıcı")

                if (totalMatchsticks > 0) {
                    computerMove()
                }
            } else {
                binding.tvGameStatus.text = "Lütfen önce kibrit seçin."
            }
        }
    }


    private fun createMatchsticks(count: Int) {
        // Önceki kibritleri temizleyin
        binding.glMatchsticks.removeAllViews()
        matchstickViews = mutableListOf()

        // GridLayout ayarları
        val columns = 7 // Her satırda kaç resim olacağını belirtir
        binding.glMatchsticks.columnCount = columns

        for (i in 1..count) {
            val matchstick = ImageView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply{
                    width = 60
                    height = 200
                    setMargins(8, 8, 8, 8) // Kenar boşlukları
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // Her satırda bir eşit alan
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setImageResource(R.drawable.macthh)
                setPadding(4, 4, 4, 4)
                visibility = View.VISIBLE
                isClickable = true

                //kullanıcı hamlesi
                setOnClickListener {
                    if (visibility == View.VISIBLE && userSelectedCount < maxSelectable) {
                        visibility = View.INVISIBLE
                        userSelectedCount++
                    }
                }
            }
            matchstickViews.add(matchstick)
            binding.glMatchsticks.addView(matchstick)
        }

        totalMatchsticks = count
    }

    private fun showDifficultyBottomSheetDialog() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val dialogView = layoutInflater.inflate(R.layout.difficulty_screen_dialog, null)
        bottomSheetDialog.setContentView(dialogView)

        val easyLayout = dialogView.findViewById<LinearLayout>(R.id.easyLayout)
        val mediumLayout = dialogView.findViewById<LinearLayout>(R.id.mediumLayout)
        val hardLayout = dialogView.findViewById<LinearLayout>(R.id.hardLayout)

        val onClickListener = { difficulty: Int ->
            when (difficulty) {
                0 -> {
                    createMatchsticks(21)
                    bottomSheetDialog.dismiss()
                }

                1 -> {
                    createMatchsticks(35)
                    bottomSheetDialog.dismiss()
                }

                2 -> {
                    createMatchsticks(42)
                    bottomSheetDialog.dismiss()
                }
            }
            bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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

        //  bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
    }


    // Basit bir strateji: Bilgisayar kalan kibrit sayısına göre hareket eder
    private fun computerMove() {
        val matchsticksTaken = if (totalMatchsticks <= 3) totalMatchsticks else (1..3).random()
        for (i in 0 until matchsticksTaken) {
            if (totalMatchsticks > 0) {
                matchstickViews[totalMatchsticks - 1].visibility = View.INVISIBLE
                totalMatchsticks--
            }
        }
        binding.tvGameStatus.text = "Bilgisayar $matchsticksTaken kibrit aldı."
        checkGameStatus("Bilgisayar")
    }

    private fun checkGameStatus(player: String) {
        if (totalMatchsticks <= 0) {
            binding.tvGameStatus.text = "$player oyunu kaybetti!"
            binding.btnUserMove.isEnabled = false
        }
    }

}
