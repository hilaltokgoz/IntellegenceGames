package com.gloory.intellegencegames.view.custom
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.random.Random

class GuessTheNumberGameFragment : Fragment() {
    private var _binding: FragmentGuessTheNumberGameBinding? = null
    private val binding get() = _binding!!

    private var randomNumber = 0
    private var attempts = 5

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
        randomNumber = Random.nextInt(1, 101)
    }
    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val bindingDialog =
            FragmentGuessthenumbergameDialogBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog.apply {
            easyImage.setOnClickListener {
                dialog.dismiss()
            }
            mediumImage.setOnClickListener {
                dialog.dismiss()
            }
            hardImage.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()
    }


}

