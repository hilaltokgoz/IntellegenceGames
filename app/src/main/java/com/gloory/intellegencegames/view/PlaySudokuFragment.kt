package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.DifficultyScreenDialogBinding
import com.gloory.intellegencegames.databinding.FragmentPlaySudokuBinding
import com.gloory.intellegencegames.game.Cell
import com.gloory.intellegencegames.game.SudokuDifficulty
import com.gloory.intellegencegames.view.custom.SudokuBoardView
import com.gloory.intellegencegames.viewmodel.PlaySudokuViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class PlaySudokuFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private var _binding: FragmentPlaySudokuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaySudokuViewModel by viewModels()
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
        //fragmentta bi şey yaratıldığında  aktarılır Observer:Gözlemci,seçilen hücre canlı verilene ne olduğu gözlemlenir.
        viewModel.sudokuGame.selectedCellLiveData.observe(
            viewLifecycleOwner,
            Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(
            viewLifecycleOwner,
            Observer { updateCells(it) }) //hücre için observer tanımı

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
            buttonList?.forEachIndexed { index, button ->
                button.setOnClickListener {
                    viewModel.sudokuGame.handleInput(index + 1)
                }
            }
            deleteButton.setOnClickListener {
                viewModel.sudokuGame.delete()
            }
            checkButton.setOnClickListener {
                checkConflicts()

            }
        }

    }

    // Sudoku tamamlandığında true döner, aksi takdirde false
    // Tüm hücrelerin doğru şekilde doldurulduğunu kontrol eden kod
    private fun isGameCompleted(): Boolean {
        return viewModel.sudokuGame.isGameCompleted()
    }
    private fun showGameCompletedDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_game_completed, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val playAgainButton = dialogView.findViewById<Button>(R.id.btn_play_again)
        val exitButton = dialogView.findViewById<Button>(R.id.btn_exit)

        playAgainButton.setOnClickListener {
            alertDialog.dismiss()
            viewModel.sudokuGame.setDifficulty(SudokuDifficulty.EASY) // Veya kullanıcı tarafından seçilen zorluk seviyesine göre
        }

        exitButton.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(R.id.homeFragment)

        }
    }
    private fun showDifficultyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.difficulty_screen_dialog, null)

        val easyLayout = dialogView.findViewById<LinearLayout>(R.id.easyLayout)
        val mediumLayout = dialogView.findViewById<LinearLayout>(R.id.mediumLayout)
        val hardLayout = dialogView.findViewById<LinearLayout>(R.id.hardLayout)

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the height to wrap_content
        dialogView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val onClickListener = { difficulty: SudokuDifficulty ->
            viewModel.sudokuGame.setDifficulty(difficulty)
            bottomSheetDialog.dismiss() // Close the dialog after selection
        }

        easyLayout.setOnClickListener { onClickListener(SudokuDifficulty.EASY) }
        mediumLayout.setOnClickListener { onClickListener(SudokuDifficulty.MEDIUM) }
        hardLayout.setOnClickListener { onClickListener(SudokuDifficulty.HARD) }

        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()
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
        checkConflicts()

        if (viewModel.sudokuGame.isGameCompleted()) {
            showGameCompletedDialog()
        }
    }

    private fun checkConflicts() {
        binding.sudokuBoardView.checkConflictsAndDraw()
        if (viewModel.sudokuGame.isGameCompleted()) {
            showGameCompletedDialog()
        }

    }
    override fun onGameCompleted() {
        if (isGameCompleted()) {
            showGameCompletedDialog()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}