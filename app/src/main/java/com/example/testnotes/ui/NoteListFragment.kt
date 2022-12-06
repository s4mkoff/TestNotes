package com.example.testnotes.ui



import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.testnotes.BaseApplication
import com.example.testnotes.R
import com.example.testnotes.data.SettingsDataStore
import com.example.testnotes.databinding.FragmentNoteListBinding
import com.example.testnotes.model.Note
import com.example.testnotes.ui.adapter.ItemSwipe
import com.example.testnotes.ui.adapter.NoteListAdapter
import com.example.testnotes.ui.viewmodel.NoteViewModel
import com.example.testnotes.ui.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.launch


class NoteListFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as BaseApplication).database.noteDao()
        )
    }

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var allNotesList: List<Note>
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var adapter: NoteListAdapter

    private var isLinearLayoutManager = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        allNotesList = mutableListOf()
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) { value ->
            isLinearLayoutManager = value
            chooseLayout()
            activity?.invalidateOptionsMenu()
        }
        adapter = NoteListAdapter { note ->
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
            addNoteFab.setOnClickListener {
                findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment)
            }
        }
        chooseLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        setIcon(layoutButton)
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return
        menuItem.icon =
            if (isLinearLayoutManager)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid_layout_24)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_linear_layout_24)
    }

    private fun chooseLayout() {
        if (isLinearLayoutManager) {
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
        }
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_switch_layout -> {
                isLinearLayoutManager = !isLinearLayoutManager
                chooseLayout()
                setIcon(item)
                lifecycleScope.launch {
                    settingsDataStore.saveLayoutToPreferencesStore(isLinearLayoutManager, requireContext())
                }
                return true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}