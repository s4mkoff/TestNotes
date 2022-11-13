package com.example.testnotes.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testnotes.BaseApplication
import com.example.testnotes.R
import com.example.testnotes.databinding.FragmentNoteDetailBinding
import com.example.testnotes.model.Note
import com.example.testnotes.ui.viewmodel.NoteViewModel
import com.example.testnotes.ui.viewmodel.NoteViewModelFactory


class NoteDetailFragment : Fragment() {

    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

    private var _binding: FragmentNoteDetailBinding? = null

    private lateinit var note: Note

    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as BaseApplication).database.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (id>0) {
                    updateNote()
                    findNavController().navigate(
                        R.id.action_noteDetailFragment_to_noteListFragment
                    )
                } else {
                    addNote()
                    findNavController().navigate(
                        R.id.action_noteDetailFragment_to_noteListFragment
                    )
                }
            }
        })
        bindNote(id)
    }

    private fun addNote() {
        if (isValidText()) {
            viewModel.addNote(
                binding.editNote.text.toString()
            )
        }
    }

//    private fun deleteNote(note: Note) {
//        viewModel.deleteNote(note)
//        findNavController().navigate(
//            R.id.action_noteDetailFragment_to_noteListFragment
//        )
//    }

    private fun bindNote(id: Int) {
        if (id>0) {
            viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) {
                selectedNote ->
                        binding.apply{
                            editNote.setText(selectedNote.text, TextView.BufferType.SPANNABLE)
                        }
            }
        }
    }

    private fun updateNote() {
        if (isValidText()) {
            viewModel.updateNote(
                id = navigationArgs.id,
                text = binding.editNote.text.toString()
            )
        }
    }

    private fun isValidText() = viewModel.isValidEntry(
        binding.editNote.text.toString()
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
