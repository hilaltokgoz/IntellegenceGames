package com.gloory.intellegencegames.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.databinding.FragmentTicTacToeBinding



class TicTacToeFragment : Fragment() {
    private var _binding: FragmentTicTacToeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTicTacToeBinding.inflate(inflater, container, false)

        val dialogScreen=TictactoeDialog()
        dialogScreen.show( parentFragmentManager,"TictactoeDialog")

        return binding.root
    }
}