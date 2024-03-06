package com.gloory.intellegencegames.view

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.DifficultyScreenDialogBinding
import com.gloory.intellegencegames.databinding.FragmentGuessTheNumberGameBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.random.Random


class GuessTheNumberGameFragment : Fragment() {
    private var _binding: FragmentGuessTheNumberGameBinding? = null
    private val binding get() = _binding!!

    var realNumberList: List<String> = listOf()
    var guessNumberList: List<String> = listOf()

    val randomNumber3Digits = Random.nextInt(100, 1000)
    val randomNumber4Digits = Random.nextInt(1000, 10000)
    val randomNumber5Digits = Random.nextInt(10000, 100000)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuessTheNumberGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog()
    }
    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val bindingDialog =
            DifficultyScreenDialogBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.apply {
            easyImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters =
                    arrayOf(InputFilter.LengthFilter(3)) // 3 basamaklı sayı girildi.

            }
            mediumImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(4))


            }
            hardImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(5))


            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun digit3Process() {
        //gerçek sayı parçalandı listeye kondu
        val digitHundredReal = randomNumber3Digits.toString().substring(0, 1)
        val digitTensReal = randomNumber3Digits.toString().substring(1, 2)
        val digitOnesReal = randomNumber3Digits.toString().substring(2, 3)
        realNumberList = listOf(digitHundredReal, digitTensReal, digitOnesReal)

        binding.apply {
            addButton.setOnClickListener {
                val guessNumber = binding.guessNumberText

                guess1.text = guessNumber.toString()
                //guess1 al ve parçala
                val digitHundredGuess = guessNumber.text.toString().substring(0, 1)
                val digitTensGuess = guessNumber.text.toString().substring(1, 2)
                val digitOnesGuess = guessNumber.text.toString().substring(2, 3)
                guessNumberList = listOf(digitHundredGuess, digitTensGuess, digitOnesGuess)
            }
        }
        var plusCount = 0
        var minusCount = 0

        for ((index, value) in realNumberList.withIndex()) {
            if (guessNumberList.contains(value)) {  // tahmin edilen rakam gerçek listede var
                if (guessNumberList[index] == value) {    //hem indeks hem rakam var
                    plusCount++
                } else {
                    minusCount++
                }
            }
        }
        //Sonuçlar basılacak
          binding.hint1.text = "$minusCount $plusCount"
    }

}

