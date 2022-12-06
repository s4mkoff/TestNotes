package com.example.testnotes.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.testnotes.data.NoteDao
import com.example.testnotes.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NoteViewModel(
    private val noteDao: NoteDao
): ViewModel() {
    val allNotes = noteDao.getNotes().asLiveData()

    fun retrieveNote(id: Int): LiveData<Note> {
        return noteDao.getNote(id).asLiveData()
    }


    fun addNote(
        text: String,
    ) {
        val note = Note(
            text = text,
            date = getDate()
        )
        Log.v("ViewModel", "Created new note ${note.id}")
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }


    private fun getDate(): String {
        val formatter = SimpleDateFormat("HH:mm yyyy.MM.dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time)
    }

    fun updateNote(
        id: Int,
        text: String,
    ) {
        val note = Note(
            id = id,
            text = text,
            date = getDate()
        )
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    fun deleteNote(note: Note) {
        Log.v("ViewModel", "Deleting note ${note.id}")
        viewModelScope.launch(Dispatchers.IO){
            noteDao.delete(note)
        }
    }

    fun detectChanges(newText: String, oldText: String): Boolean {
        return newText != oldText
    }

    fun isValidEntry(text : String): Boolean {
        return text.isNotBlank()
    }

}

class NoteViewModelFactory(private val noteDao: NoteDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}