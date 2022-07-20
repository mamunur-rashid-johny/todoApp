package com.qubelex.todoapp.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteDialog:DialogFragment() {
    private val viewModel:DeleteViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm delete")
            .setMessage("Do you want to delete note?")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Yes"){ _,_->
                viewModel.onConDelete()
            }
            .create()

}
