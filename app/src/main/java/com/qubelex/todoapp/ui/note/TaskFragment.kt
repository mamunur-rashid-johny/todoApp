package com.qubelex.todoapp.ui.note

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

        val noteAdapter = NoteAdapter(this)

        binding.apply {
            taskRec.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                //for swipe to delete purpose
                ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean { return false }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val note = noteAdapter.currentList[viewHolder.adapterPosition]
                        viewModel.onSwipeNote(note)
                    }

                }).attachToRecyclerView(taskRec)
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

        //for receive event from viewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.noteEvent.collect{event->
                when(event){
                    is NoteViewModel.NoteEvent.ShowOnNoteDelete->{
                        Snackbar.make(requireView(),"Note Deleted",Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDelete(event.note)
                            }.show()
                    }
                }
            }
        }
    }

    override fun onItemClick(note: Note) {
        viewModel.onNoteSelected(note)
    }

    override fun onItemCheck(note: Note, isCheck: Boolean) {
        viewModel.onNoteChecked(note,isCheck)
    }


}
