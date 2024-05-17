package com.gloory.intellegencegames.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.databinding.FragmentPuzzleBinding

class PuzzleFragment : Fragment() {
    private var _binding: FragmentPuzzleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun onImageCameraClicked(view: View) {}
    fun onImageGallerClicked(view: View) {}

}