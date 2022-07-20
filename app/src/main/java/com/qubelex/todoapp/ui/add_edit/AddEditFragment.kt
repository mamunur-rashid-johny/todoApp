package com.qubelex.todoapp.ui.add_edit

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.qubelex.todoapp.R
import com.qubelex.todoapp.databinding.FragmentAddEditTaskBinding
import com.qubelex.todoapp.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFragment:Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel:AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            edTitle.setText(viewModel.taskTitle)
            edDes.setText(viewModel.taskDes)
            taskCreatedDate.isVisible = viewModel.note!=null
            taskCreatedDate.text = viewModel.note?.formattedDate
            if (viewModel.taskImportance){
                noteImportance.load(R.drawable.ic_fav_fill)
            }else{
                noteImportance.load(R.drawable.ic_fav)
            }

            edTitle.addTextChangedListener { text->
                viewModel.taskTitle = text.toString()
            }

            edDes.addTextChangedListener {
                viewModel.taskDes = it.toString()
            }

            noteImportance.setOnClickListener {
                viewModel.taskImportance = !viewModel.taskImportance
                noteImportance.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.anim_fav))
                if (viewModel.taskImportance){
                    noteImportance.load(R.drawable.ic_fav_fill)
                }else{
                    noteImportance.load(R.drawable.ic_fav)
                }
            }

            taskSaveFab.setOnClickListener {
                viewModel.onSaveClick()
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditEvent.collect{ event ->
                when(event){
                    is AddEditViewModel.AddEditEvent.NavigateWithResult -> {
                        binding.edTitle.clearFocus()
                        binding.edDes.clearFocus()
                        setFragmentResult(
                            "add_edit_note_result", bundleOf("add_edit_note_result" to event.result )
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditViewModel.AddEditEvent.ShowInvalidInputMsgTitle -> {
                        Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive

            }
        }


    }

}
