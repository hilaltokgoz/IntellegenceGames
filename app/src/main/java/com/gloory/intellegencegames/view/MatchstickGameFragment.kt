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
import androidx.core.content.ContextCompat
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
   // private var remainingMatchsticks = totalMatchsticks

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

        // Kullanıcı hamlesi
        binding.btnUserMove.setOnClickListener {
            if (userSelectedCount > 0) {
                //totalMatchsticks -= userSelectedCount//1
                userSelectedCount = 0
                // Kibrit sayısını kontrol et
                if (totalMatchsticks > 0) {
                    // Oyun bitmeden önce bilgisayarın hamlesi
                    computerMove()
                } else {
                    // Oyun bitmişse sonucu kontrol et
                    checkGameStatus("Kullanıcı")  // Kullanıcının hareketinden sonra oyun bitişini kontrol et
                }
            } else {
                binding.tvGameStatus.text = "Lütfen önce kibrit seçin."
            }
        }

        binding.ivSettings.setOnClickListener {
            showGameRulesDialog()
        }
    }

    private fun showGameRulesDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Oyun Kuralları")
        builder.setMessage("\n1. Oyun, bilgisayara karşı oynanır.\n" +
                "2. Sırası gelen oyuncu 1,2 veya 3 adet kibrit alabilir.\n" +
                "3. Son kibriti alan oyuncu kaybeder.\n\nİyi eğlenceler!")

        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        // PositiveButton'a erişim ve renk değiştirme
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue_dark))
    }


    private fun createMatchsticks(count: Int) {
        // Önceki kibritleri temizleyin
        binding.glMatchsticks.removeAllViews()
        matchstickViews = mutableListOf()

        // Kibrit sayısını doğru ayarladığınızdan emin olun
        totalMatchsticks = count
      //  remainingMatchsticks = count

        // GridLayout ayarları
        val columns = 7 // Her satırda kaç resim olacağını belirtir
        binding.glMatchsticks.columnCount = columns

        for (i in 1..count) {
            val matchstick = ImageView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 60
                    height = 200
                    setMargins(8, 8, 8, 8) // Kenar boşlukları
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // Her satırda eşit alan
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setImageResource(R.drawable.macthh)
                setPadding(4, 4, 4, 4)
                visibility = View.VISIBLE
                isClickable = false // Başlangıçta tıklanamaz olarak ayarla
            }
            matchstickViews.add(matchstick)
            binding.glMatchsticks.addView(matchstick)
        }

        //totalMatchsticks = count
        updateMatchstickClickability()
    }

    private fun updateMatchstickClickability() {

        if (totalMatchsticks <= 0) return

        // Tüm kibritlerin tıklanabilirliğini kapat
        matchstickViews.forEach { it.isClickable = false }

        // Kalan son görünen 3 kibrit tıklanabilir olsun
        var visibleCount = 0
        for (i in matchstickViews.size - 1 downTo 0) {
            if (matchstickViews[i].visibility == View.VISIBLE) {
                matchstickViews[i].isClickable = true
                matchstickViews[i].setOnClickListener {
                    if (userSelectedCount < maxSelectable) {
                        matchstickViews[i].visibility = View.INVISIBLE
                        userSelectedCount++
                        totalMatchsticks--  // Decrease remaining matchsticks

                        // Kibrit sayısını güncelleyip oyunun bitip bitmediğini kontrol et
                        if (totalMatchsticks > 0) {
                            updateMatchstickClickability()
                        } else {
                            checkGameStatus("Kullanıcı")
                        }
                    }
                }
                visibleCount++
                if (visibleCount == 3) break // Son 3 kibrite ulaşıldığında dur
            }
        }
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
           // difficultyLevel = "normal"
            createMatchsticks(35)
            bottomSheetDialog.dismiss()
        }
        hardLayout.setOnClickListener {
            //difficultyLevel = "hard"
            createMatchsticks(42)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.show()
    }
    // Basit bir strateji: Bilgisayar kalan kibrit sayısına göre hareket eder
    // Bilgisayarın hamlesi
    private fun computerMove() {
        if (totalMatchsticks <= 0) return

        val matchsticksTaken: Int = when {
            totalMatchsticks == 2 -> 1
            totalMatchsticks == 3 -> 2
            totalMatchsticks <= 3 -> totalMatchsticks
            totalMatchsticks % 4 == 1 -> 1
            totalMatchsticks % 4 == 2 -> 2
            totalMatchsticks % 4 == 3 -> 3
            else -> 1
        }

        // Kibritleri al
        var taken = 0
        for (i in matchstickViews.size - 1 downTo 0) {
            if (matchstickViews[i].visibility == View.VISIBLE) {
                matchstickViews[i].visibility = View.INVISIBLE
                totalMatchsticks--  // Decrease remaining matchsticks
                taken++
                if (taken == matchsticksTaken) break
            }
        }

        binding.tvGameStatus.text = "Bilgisayar $matchsticksTaken kibrit aldı."
       // userSelectedCount = 0

        // Oyun bitip bitmediğini kontrol et
        if (totalMatchsticks > 0) {
            updateMatchstickClickability()
        } else {
            checkGameStatus("Bilgisayar")
        }
    }


    // Oyun durumunu kontrol et
    private fun checkGameStatus(player: String) {
        // Eğer tüm kibritler bitti ise
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
            // Bilgisayar kaybetti
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
/*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

 */
}
