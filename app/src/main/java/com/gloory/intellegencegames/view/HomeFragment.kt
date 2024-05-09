package com.gloory.intellegencegames.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentHomeBinding
import com.gloory.intellegencegames.game.HomePageCustomAdapter
import com.gloory.intellegencegames.game.HomePageItem

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val recyclerview = binding.recyclerview
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        val adapter = HomePageCustomAdapter(HomePageItem.getAllList()) {
            when (it) {
                HomePageItem.SUDOKU-> {
                    findNavController().navigate(R.id.action_homeFragment_to_playSudokuFragment)
                }
                HomePageItem.ESLESTIRME -> {
                    findNavController().navigate(R.id.action_homeFragment_to_matchingGameFragment)
                }
                HomePageItem.TICTACTOE -> {
                    findNavController().navigate(R.id.action_homeFragment_to_ticTacToeFragment)
                }
                HomePageItem.GUESSTHENUMBER-> {
                    findNavController().navigate(R.id.action_homeFragment_to_guessTheNumberGameFragment)
                }
                HomePageItem.KELİMEAVI-> {
                    findNavController().navigate(R.id.action_homeFragment_to_kelimeAviFragment)
                }
                HomePageItem.ANAGRAM-> {
                    findNavController().navigate(R.id.action_homeFragment_to_anagramGameFragment)
                }
            }
        }
        recyclerview.adapter = adapter
        return binding.root
    }
}