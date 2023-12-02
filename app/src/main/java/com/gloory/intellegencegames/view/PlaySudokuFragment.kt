package com.gloory.intellegencegames.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gloory.intellegencegames.databinding.FragmentPlaySudokuBinding
import com.gloory.intellegencegames.view.custom.SudokuBoardView
import com.gloory.intellegencegames.viewmodel.PlaySudokuViewModel


class PlaySudokuFragment : Fragment(), SudokuBoardView.OnTouchListener {
    private var _binding: FragmentPlaySudokuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaySudokuViewModel//view model görüntülenmek içn çağrılır.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaySudokuBinding.inflate(inflater)
        return binding.root

        binding.sudokuBoardView.registerListener(this)

        //fragmentta bi şey yaratıldığında  aktarılır
     viewModel = ViewModelProviders.of(this)[PlaySudokuViewModel::class.java]
        //Observer:Gözlemci,seçilen hücre canlı verilene ne olduğu gözlemlenir.
        viewModel.sudokuGame.selectedCellLiveData.observe(
            viewLifecycleOwner,
            Observer { updateSelectedCellUI(it) })
    } //.let, cell boş değilse çalışır

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    //interface fonk için override edildi
    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

}