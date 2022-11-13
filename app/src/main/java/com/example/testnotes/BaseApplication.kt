package com.example.testnotes

import android.app.Application
import com.example.testnotes.data.NoteDatabase

class BaseApplication: Application() {
    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this)}
}