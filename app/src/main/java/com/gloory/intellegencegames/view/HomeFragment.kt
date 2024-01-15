package com.gloory.intellegencegames.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            sudokuImage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_playSudokuFragment)
            }
            matchingImage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_matchingGameFragment)
            }
            tictactoeImage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_ticTacToeFragment)
            }
        }
        return binding.root
    }
}