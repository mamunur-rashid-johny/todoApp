package com.qubelex.todoapp.ui.note

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat.animate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.qubelex.todoapp.R
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.databinding.FragmentTaskBinding
import com.qubelex.todoapp.utils.SortOrder
import com.qubelex.todoapp.utils.exhaustive
import com.qubelex.todoapp.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task), NoteAdapter.OnNoteItemClick {
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var searchView: SearchView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTaskBinding.bind(view)

        val noteAdapter = NoteAdapter(this)

        binding.apply {
            taskRec.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                //for swipe to delete purpose
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val note = noteAdapter.currentList[viewHolder.adapterPosition]
                        viewModel.onSwipeNote(note)
                    }

                }).attachToRecyclerView(taskRec)
            }

            taskAddFab.setOnClickListener {
                viewModel.onAddNewNote()
            }
        }

        setFragmentResultListener("add_edit_note_result") { _, bundle ->
            val result = bundle.getInt("add_edit_note_result")
            viewModel.onAddEditResult(result)

        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            binding.tvNoDataFound.isVisible = it.isEmpty()
            noteAdapter.submitList(it)
        }


//        //inflate menu
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.task_fragment_menu, menu)
                val searchItem = menu.findItem(R.id.search_action)
                searchView = searchItem.actionView as SearchView
                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }
                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

                //fetch save setting and apply to menu
                viewLifecycleOwner.lifecycleScope.launch {
                    menu.findItem(R.id.hide_completed_tasks).isChecked =
                        viewModel.settingFlow.first().hideCompleted
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
                        viewModel.deleteAllCompleted()
                        true
                    }
                    else -> false
                }
            }
        }, this.viewLifecycleOwner)

        //for receive event from viewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.noteEvent.collect { event ->
                when (event) {
                    is NoteViewModel.NoteEvent.ShowOnNoteDelete -> {
                        Snackbar.make(requireView(), "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDelete(event.note)
                            }.show()
                    }
                    is NoteViewModel.NoteEvent.NavigateAddNoteScreen -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToAddEditFragment(
                            note = null,
                            title = "New Note"
                        )
                        findNavController().navigate(action)
                    }
                    is NoteViewModel.NoteEvent.NavigateToEditScreen -> {
                        val action =
                            TaskFragmentDirections.actionTaskFragmentToAddEditFragment(
                                note = event.note,
                                title = "Edit Note"
                            )
                        findNavController().navigate(action)
                    }
                    is NoteViewModel.NoteEvent.ShowConMsg -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    NoteViewModel.NoteEvent.NavigateToDeleteDialog -> {
                        val action = TaskFragmentDirections.actionGlobalDeleteDialog()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }


    }

    override fun onItemClick(note: Note) {
        viewModel.onNoteSelected(note)
    }

    override fun onItemCheck(note: Note, isCheck: Boolean) {
        viewModel.onNoteChecked(note, isCheck)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}
