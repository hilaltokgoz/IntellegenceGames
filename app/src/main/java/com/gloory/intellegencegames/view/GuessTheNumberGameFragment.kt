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

    var realNumberList: List<Char> = listOf()
    var guessNumberList: List<Char> = listOf()

    val randomNumber3Digits = randomNumber(3)
    val randomNumber4Digits = randomNumber(4)
    val randomNumber5Digits = randomNumber(5)


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
                digit3Process()
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
        realNumberList = randomNumber3Digits.toString().toCharArray().toList()

        println(" randomDigit  $randomNumber3Digits")

        binding.apply {
            addButton.setOnClickListener {
                val guessNumber = binding.guessNumberText.text.toString()
                if (guessNumber.length == 3) {
                    binding.guessNumberText.setText("")
                    guess1.text = "${guess1.text}$guessNumber\n"
                    //guess1 al ve parçala
                    guessNumberList = guessNumber.toCharArray().toList()
                    var hint = ""

                    for ((index, value) in realNumberList.withIndex()) {
                        if (guessNumberList.contains(value)) {  // tahmin edilen rakam gerçek listede var
                            if (guessNumberList[index] == value) {    //hem indeks hem rakam var

                                hint += "+"
                            } else {
                                hint += "-"
                            }
                        }
                    }
                    //Sonuçlar basılacak
                    hint1.text = "${hint1.text}$hint\n"
                }

            }
        }

    }

    fun randomNumber( digitCount:Int): Int {
        var uniqueNumber: Int
        do {
            uniqueNumber = when(digitCount){
                3 ->  (100..1000).random()
                4 ->  (1000..10000).random()
                5 ->  (10000..100000).random()
                else -> return -1
            }
        } while (uniqueNumber.toString().toSet().size != digitCount)

        return uniqueNumber
    }


}

