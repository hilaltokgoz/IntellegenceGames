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
import com.gloory.intellegencegames.game.HomePage_Database

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
        val adapter = HomePageCustomAdapter(HomePage_Database.getList())

        adapter.setOnItemClickListener { position ->
            when (position) {
                0 -> {
                    findNavController().navigate(R.id.action_homeFragment_to_playSudokuFragment)
                }
                1 -> {
                    findNavController().navigate(R.id.action_homeFragment_to_matchingGameFragment)
                }
                2 -> {
                    findNavController().navigate(R.id.action_homeFragment_to_ticTacToeFragment)
                }
                else -> {
                    findNavController().navigate(R.id.action_homeFragment_to_playSudokuFragment)
                }
            }
        }
        recyclerview.adapter = adapter
        return binding.root
    }
}