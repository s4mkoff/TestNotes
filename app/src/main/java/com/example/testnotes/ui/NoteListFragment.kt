package com.example.testnotes.ui



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testnotes.BaseApplication
import com.example.testnotes.R
import com.example.testnotes.databinding.FragmentNoteListBinding
import com.example.testnotes.model.Note
import com.example.testnotes.ui.adapter.ItemSwipe
import com.example.testnotes.ui.adapter.NoteListAdapter
import com.example.testnotes.ui.viewmodel.NoteViewModel
import com.example.testnotes.ui.viewmodel.NoteViewModelFactory


class NoteListFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as BaseApplication).database.noteDao()
        )
    }

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var allNotesList: List<Note>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        allNotesList = mutableListOf()
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = NoteListAdapter { note ->
            val action = NoteListFragmentDirections
                .actionNoteListFragmentToNoteDetailFragment(note.id)
            findNavController().navigate(action)
        }
        viewModel.allNotes.observe(viewLifecycleOwner) { listOfNotes ->
            listOfNotes.let {
                allNotesList = it
                adapter.submitList(it)
            }
            ItemTouchHelper(ItemSwipe { viewHolder: RecyclerView.ViewHolder ->
                val position = viewHolder.adapterPosition
                viewModel.deleteNote(allNotesList[position])
            }).attachToRecyclerView(binding.recyclerView)
        }
        binding.apply {
            recyclerView.adapter = adapter
            addNoteFab.setOnClickListener {
                findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}