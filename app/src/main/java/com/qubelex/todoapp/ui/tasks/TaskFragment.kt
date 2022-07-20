package com.qubelex.todoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qubelex.todoapp.R
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.databinding.FragmentTaskBinding
import com.qubelex.todoapp.utils.SortOrder
import com.qubelex.todoapp.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task),NoteAdapter.OnNoteItemClick {
    private val viewModel: NoteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTaskBinding.bind(view)

        val data = listOf<Note>(
            Note("Dummy title to show some text.", "2", false),
            Note("Dummy title to some text.", "3", false)
        )

        val noteAdapter = NoteAdapter()

        binding.apply {
            taskRec.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        noteAdapter.submitList(data)

        //inflate menu
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.task_fragment_menu, menu)
                val searchItem = menu.findItem(R.id.search_action)
                val searchView = searchItem.actionView as SearchView
                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

                //fetch save setting and apply to menu
                viewLifecycleOwner.lifecycleScope.launch {
                    menu.findItem(R.id.hide_completed_tasks).isChecked = viewModel.settingFlow.first().hideCompleted
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sort_by_date -> {
                        viewModel.updateSortOrder(SortOrder.BY_DATE)
                        true
                    }
                    R.id.sort_by_title -> {
                        viewModel.updateSortOrder(SortOrder.BY_NAME)
                        true
                    }
                    R.id.hide_completed_tasks -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.updateHideCompleted(menuItem.isChecked)
                        true
                    }
                    R.id.delete_all_completed_task -> {

                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onItemClick(note: Note) {
        viewModel.onNoteSelected(note)
    }

    override fun onItemCheck(note: Note, isCheck: Boolean) {
        viewModel.onNoteChecked(note,isCheck)
    }


}
