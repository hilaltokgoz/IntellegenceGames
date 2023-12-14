package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentPlaySudokuBinding
import com.gloory.intellegencegames.game.Cell
import com.gloory.intellegencegames.game.SudokuDifficulty
import com.gloory.intellegencegames.view.custom.SudokuBoardView
import com.gloory.intellegencegames.viewmodel.PlaySudokuViewModel


class PlaySudokuFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private var _binding: FragmentPlaySudokuBinding? = null
    private val binding get() = _binding!!

    private lateinit var sudokuView: SudokuBoardView

    private lateinit var viewModel: PlaySudokuViewModel//view model görüntülenmek içn çağrılır.
    private var buttonList: List<Button>? = null

    var selectedDifficulty = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaySudokuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sudokuBoardView.registerListener(this)

        showDifficultyDialog()


        //fragmentta bi şey yaratıldığında  aktarılır
        viewModel = ViewModelProviders.of(this)[PlaySudokuViewModel::class.java]
        //Observer:Gözlemci,seçilen hücre canlı verilene ne olduğu gözlemlenir.
        viewModel.sudokuGame.selectedCellLiveData.observe(
            viewLifecycleOwner,
            Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(
            viewLifecycleOwner,
            Observer { updateCells(it) }) //hücre için observer tanımı
        viewModel.sudokuGame.isTakingNotesLiveData.observe(viewLifecycleOwner,
            Observer {
                updateNoteTakingUI(it)
            })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(viewLifecycleOwner,
            Observer {
                updateHighlightedKeys(it)
            })

        binding.apply {
            buttonList = (listOf(
                oneButton,
                twoButton,
                threeButton,
                fourButton,
                fiveButton,
                sixButton,
                sevenButton,
                eighthButton,
                nineButton
            ))
            buttonList!!.forEachIndexed { index, button ->
                button.setOnClickListener {
                    viewModel.sudokuGame.handleInput(index + 1)
                }
            }
            notesButton.setOnClickListener {
                viewModel.sudokuGame.changeNoteTakingState()
            }
            deleteButton.setOnClickListener {
                viewModel.sudokuGame.delete()
            }
            checkButton.setOnClickListener {
                checkConflicts()
            }
        }
    }

    //zorluk derecesini belirlemek için alertDialog kullanıldı.
    private fun showDifficultyDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("Zorluk Seviyesini Seçiniz...")
            .setSingleChoiceItems(
                arrayOf("Kolay", "Orta", "Zor"), 0
            ) { _, which ->
                selectedDifficulty = which
            }
            .setPositiveButton("Tamam") { dialog, which ->
                val difficulty = when (selectedDifficulty) {
                    0 -> {
                        SudokuDifficulty.EASY
                    }
                    1 -> {
                        SudokuDifficulty.MEDIUM
                    }
                    2 -> {
                        SudokuDifficulty.HARD
                    }
                    else -> SudokuDifficulty.EASY
                }
                viewModel.sudokuGame.setDifficulty(difficulty)
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    //let bloğu boş değilse günceller.
    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color =
            if (it) ContextCompat.getColor(requireContext(), R.color.teal_200) else Color.LTGRAY
        //not alınırsa rengi değişecek
        binding.notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        buttonList?.forEachIndexed { index, button ->
            //seçiliyse seçilmiş renklere, değilse gri olsun
            val color = if (set.contains(index + 1)) ContextCompat.getColor(
                requireContext(),
                R.color.teal_200
            ) else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    //hücre listesine erişip hücreleri günceller
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    //.let, cell boş değilse çalışır
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    //
    //interface fonk için override edildi
    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    fun checkButton() {
        binding.checkButton.setOnClickListener {

            /***
            Kontrol et butonuna tıklandığında etrafında bulunan açık gri renkli hücrelerde yer alan
            sayı tekrarlandıysa o hücreleri kırmızı yap.
             ***/
        }
    }
    private fun checkConflicts() {
        sudokuView.checkConflictsAndDraw(Canvas())
    }
}