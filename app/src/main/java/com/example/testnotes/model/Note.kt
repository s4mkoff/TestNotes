package com.example.testnotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_database")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val date: String
)
