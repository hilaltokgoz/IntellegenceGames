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
            }
            mediumImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(4))
                digit4Process()

            }
            hardImage.setOnClickListener {
                dialog.dismiss()
                binding.guessNumberText.filters = arrayOf(InputFilter.LengthFilter(5))
                digit5Process()

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

    private fun digit5Process() {
        //gerçek sayı parçalandı listeye kondu
        realNumberList = randomNumber5Digits.toString().toCharArray().toList()

        println(" randomDigit  $randomNumber5Digits")

        binding.apply {
            addButton.setOnClickListener {
                val guessNumber = binding.guessNumberText.text.toString()
                if (guessNumber.length == 5) {
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

    fun showMessageBox(){
        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_result_guessnumber,null)
        val resultTitleText = view.findViewById<TextView>(R.id.resultTitleText)
        val resultText = view.findViewById<TextView>(R.id.resultText)
        val  button = view.findViewById<Button>(R.id.dialogDismiss_button)

        if (hintList.last() == "+++") {
            resultTitleText.text = "Kazandınız!"
            resultText.text = "Sayı= ${randomNumber3Digits}" //sadece  3 basamaklı için geçerli

        } else {
            resultTitleText.text = "Kaybettiniz!"
            resultText.text = "Sayı= ${randomNumber3Digits}"
        }

        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


}

