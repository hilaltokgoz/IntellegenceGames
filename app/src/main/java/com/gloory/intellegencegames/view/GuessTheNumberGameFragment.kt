package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.DifficultyScreenDialogBinding
import com.gloory.intellegencegames.databinding.FragmentGuessTheNumberGameBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class GuessTheNumberGameFragment : Fragment() {
    private var _binding: FragmentGuessTheNumberGameBinding? = null
    private val binding get() = _binding!!
    var right = 4

    var realNumberList: List<Char> = listOf()
    var guessNumberList: List<Char> = listOf()

    var hintList: MutableList<String> = mutableListOf()

    var randomNumber3Digits = randomNumber(3)
    var randomNumber4Digits = randomNumber(4)
    var randomNumber5Digits = randomNumber(5)

    var selectDigit: Int = 0

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

    fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val bindingDialog =
            DifficultyScreenDialogBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.apply {
            easyImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters =
                    arrayOf(InputFilter.LengthFilter(3)) // 3 basamaklı sayı girildi.
                digit3Process()
                selectDigit = 3
            }
            mediumImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(4))
                digit4Process()
                selectDigit = 4

            }
            hardImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(5))
                digit5Process()
                selectDigit = 5

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

                    right--
                    rightCounterTextView.text = right.toString()

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
                    hintList.add(hint)
                    hint1.text = "${hint1.text}$hint\n"

                    if (right == 0 || hintList.last() == "+++") {
                        showMessageBox()
                    }

                }
            }
        }

    }

    private fun digit4Process() {
        //gerçek sayı parçalandı listeye kondu
        realNumberList = randomNumber4Digits.toString().toCharArray().toList()

        println(" randomDigit  $randomNumber4Digits")

        binding.apply {
            addButton.setOnClickListener {
                val guessNumber = binding.guessNumberText.text.toString()
                if (guessNumber.length == 4) {
                    right--
                    rightCounterTextView.text = right.toString()

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
                    hintList.add(hint)
                    hint1.text = "${hint1.text}$hint\n"

                    if (right == 0 || hintList.last() == "++++") {
                        showMessageBox()
                    }
                }

            }
        }

    }

    private fun digit5Process() {
        //gerçek sayı parçalandı listeye kondu
        realNumberList = randomNumber5Digits.toString().toCharArray().toList()

        println(" randomDigit  $randomNumber5Digits")

        binding.apply {
            addButton.setOnClickListener {
                val guessNumber = binding.guessNumberText.text.toString()
                if (guessNumber.length == 5) {
                    right--
                    rightCounterTextView.text = right.toString()
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
                    hintList.add(hint)
                    hint1.text = "${hint1.text}$hint\n"
                    if (right == 0 || hintList.last() == "+++++") {
                        showMessageBox()
                    }
                }

            }
        }
    }

    fun randomNumber(digitCount: Int): Int {
        var uniqueNumber: Int
        do {
            uniqueNumber = when (digitCount) {
                3 -> (100..1000).random()
                4 -> (1000..10000).random()
                5 -> (10000..100000).random()
                else -> return -1
            }
        } while (uniqueNumber.toString().toSet().size != digitCount)

        return uniqueNumber
    }


    fun showMessageBox() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_result_guessnumber, null)
        val resultTitleText = view.findViewById<TextView>(R.id.resultTitleText)
        val resultText = view.findViewById<TextView>(R.id.resultText)
        val againPlayButton = view.findViewById<Button>(R.id.againPlayButton)


        val randomNum = when (hintList.last()) {
            "+++" -> {
                resultTitleText.text = "Kazandınız!"
                randomNumber3Digits
            }
            "++++" -> {
                resultTitleText.text = "Kazandınız!"
                randomNumber4Digits
            }
            "+++++" -> {
                resultTitleText.text = "Kazandınız!"
                randomNumber5Digits
            }
            else -> {
                resultTitleText.text = "Kaybettiniz!"
                when (selectDigit) {
                    3 -> randomNumber3Digits
                    4 -> randomNumber4Digits
                    5 -> randomNumber5Digits
                    else -> 0
                }
            }
        }
        resultText.text = "Sayı= $randomNum"

        builder.setView(view)
        againPlayButton.setOnClickListener {
            builder.dismiss()
            newGame()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    fun newGame() {

        right = 4 //eski right sildirmek gerek
        binding.rightCounterTextView.text = right.toString()

        //clearList
        hintList.clear()
        binding.hint1.text = ""
        binding.guess1.text = ""
        guessNumberList = emptyList()

        //random sayı yenilenecek
        when (selectDigit) {
            3 -> {
                randomNumber3Digits = randomNumber(3)
                digit3Process()
                println(" randomDigit  $randomNumber3Digits")
            }
            4 -> {
                randomNumber4Digits = randomNumber(4)
                digit4Process()
                println(" randomDigit  $randomNumber4Digits")
            }

            5 -> {
                randomNumber5Digits = randomNumber(5)
                digit5Process()
                println(" randomDigit  $randomNumber5Digits")
            }
        }

    }

}

