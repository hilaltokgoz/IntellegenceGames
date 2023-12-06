package com.gloory.intellegencegames.view

import android.content.Context
import android.graphics.Color
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
import com.gloory.intellegencegames.view.custom.SudokuBoardView
import com.gloory.intellegencegames.viewmodel.PlaySudokuViewModel


class PlaySudokuFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private var _binding: FragmentPlaySudokuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaySudokuViewModel//view model görüntülenmek içn çağrılır.
    private var buttonList: List<Button>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaySudokuBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sudokuBoardView.registerListener(this)

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
            binding.notesButton.setOnClickListener {
                viewModel.sudokuGame.changeNoteTakingState()
            }
        }
    }
    //let bloğu boş değilse günceller.
    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        if(it){
            //notgg alınırsa rengi değişecek
            binding.notesButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
        }else{
            binding.notesButton.setBackgroundColor(Color.LTGRAY)
        }
    }

    private fun updateHighlightedKeys(it: Set<Int>?) {

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

}