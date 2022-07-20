package com.qubelex.todoapp.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.qubelex.todoapp.R
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.databinding.ItemTaskBinding

class NoteAdapter(private val onNoteItemClick: OnNoteItemClick) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCall()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class NoteViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position !=RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        onNoteItemClick.onItemClick(task)
                    }
                }

                taskCheckBox.setOnClickListener {
                    val position = adapterPosition
                    if (position !=RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        onNoteItemClick.onItemCheck(task,taskCheckBox.isChecked)
                    }
                }
            }
        }

        fun bind(note: Note) {
            binding.apply {
                taskTitle.text = note.title
                taskCheckBox.isChecked = note.completed
                taskTitle.paint.isStrikeThruText = note.completed
                if (note.important){
                  favImg.load(R.drawable.ic_fav_fill)
                }else{
                    favImg.load(R.drawable.ic_fav)
                }
            }
        }
    }

    //interface for listener on item
    interface OnNoteItemClick{
        fun onItemClick(note: Note)
        fun onItemCheck(note: Note,isCheck:Boolean)
    }

    //diffUtil
    class DiffCall : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}
