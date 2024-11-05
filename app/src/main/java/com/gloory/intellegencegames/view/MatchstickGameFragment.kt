package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
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
    private var difficultyLevel: String = "normal" // Varsayılan zorluk seviyesi

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

        easyLayout.setOnClickListener {
            createMatchsticks(21)
            bottomSheetDialog.dismiss()
        }
        mediumLayout.setOnClickListener {
            difficultyLevel = "normal"
            createMatchsticks(35)
            bottomSheetDialog.dismiss()
        }
        hardLayout.setOnClickListener {
            difficultyLevel = "hard"
            createMatchsticks(42)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.show()
    }


    // Basit bir strateji: Bilgisayar kalan kibrit sayısına göre hareket eder
    private fun computerMove() {
        val matchsticksTaken: Int = when {
            totalMatchsticks <= 1 -> totalMatchsticks // 0 veya 1 kalırsa, hepsini al
            totalMatchsticks == 2 -> 1 // 2 kalırsa, 1 al
            totalMatchsticks == 3 -> 2 // 3 kalırsa, 2 al
            totalMatchsticks % 4 == 1 -> 1 // Kullanıcının kazanmasını engellemek için 1 al
            totalMatchsticks % 4 == 2 -> 2 // Kullanıcının kazanmasını engellemek için 2 al
            totalMatchsticks % 4 == 3 -> 3 // Kullanıcı en fazla 3 alabilir

            else -> (1..3).random() // Rastgele 1, 2 veya 3 al
        }
         //kibriti gizle
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
            if (player == "Kullanıcı") {
                binding.tvGameStatus.text = "Kullanıcı oyunu kaybetti!"
                binding.btnUserMove.isEnabled = false
                showResultScreenDialog("Kullanıcı") // Kullanıcının kaybettiği bilgisini geçir
            } else {
                binding.tvGameStatus.text = "Bilgisayar oyunu kaybetti!"
                binding.btnUserMove.isEnabled = false
                showResultScreenDialog("Bilgisayar") // Bilgisayarın kaybettiği bilgisini geçir
            }
        }
    }
    private fun showResultScreenDialog(player: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_game_completed, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val playAgainButton = dialogView.findViewById<Button>(R.id.btn_play_again)
        val exitButton = dialogView.findViewById<Button>(R.id.btn_exit)
        val tvCongratulations = dialogView.findViewById<TextView>(R.id.tv_congratulations)
        val ivCongratulations = dialogView.findViewById<ImageView>(R.id.iv_congratulations)
        if (player == "Kullanıcı") {
            // Kullanıcı kaybetti
            tvCongratulations.text = "Kaybettiniz!"
            ivCongratulations.setImageResource(R.drawable.sad)
        } else {
            // Kullanıcı kazandı
            tvCongratulations.text = "Tebrikler!"
            ivCongratulations.setImageResource(R.drawable.ic_celebration)
        }
        playAgainButton.setOnClickListener {
            alertDialog.dismiss()
            resetGame()
            showDifficultyBottomSheetDialog()    //Zorluk seçme dialoğu göster
        }

        exitButton.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.homeFragment)

        }
    }
    private fun resetGame() {
        totalMatchsticks = 0
        userSelectedCount = 0
        binding.tvGameStatus.text = ""
        binding.btnUserMove.isEnabled = true // Tekrar oynamak için butonu aktif hale getir
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
