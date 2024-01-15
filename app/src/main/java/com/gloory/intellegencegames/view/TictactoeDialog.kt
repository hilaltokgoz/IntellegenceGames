package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.gloory.intellegencegames.R
import kotlinx.coroutines.NonCancellable

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 15.01.2024               │
//└──────────────────────────┘

class TictactoeDialog:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.tictacto_difficulty_screen, null))
                // Add action buttons.
                .setPositiveButton(
                    "TAMAM",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
                .setNegativeButton(
                    "ÇIKIŞ",
                    DialogInterface.OnClickListener { dialog, id ->
                        NonCancellable.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}